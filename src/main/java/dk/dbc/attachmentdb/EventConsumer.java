/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import dk.dbc.util.Timed;

/**
 * This bean consumes events from the attachment queue and
 * ships them off to the SolrDocstore service for processing.
 */
public class EventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @PersistenceContext(unitName = "attachmentdb_PU")
    EntityManager entityManager;

    /**
     * Consume next event from the attachment events queue.
     * On error, the event queue is rolled back.
     *
     * Todo: Encapsulate in transaction
     */
    public void consume() {
        try
        {
            AttachmentDbEvent event = poll ();
            if (event != null) {
                LOGGER.info ("Received one event from the queue: {}", event.toString ());
                accept (event);
            }
        } catch(Exception e) {
            LOGGER.error ("Exception when accepting event: {}".format(e.getMessage ()));
            // ToDo: rollback
        }
    }

    /**
     * Poll the attachment queue and return next event
     * @return AttachmentDbEvent if there is new events, otherwise null
     */
    @Timed
    public AttachmentDbEvent poll() {
        String consumerId = System.getProperty("CONSUMER_ID");

        final List<AttachmentDbEvent> events = entityManager
                .createNamedQuery(AttachmentDbEvent.REMOVE_EVENT_QUERY_NAME, AttachmentDbEvent.class)
                .setParameter("consumerId", consumerId)
                .getResultList();

        return events.isEmpty () ? null : events.get(0);
    }

    /**
     * Send event to SolrDocstore
     * @throws AttachmentDbEventAcceptException if the event could not be delivered to SolrDocstore
     */
    @Timed
    public void accept(AttachmentDbEvent event) throws AttachmentDbEventAcceptException {
        try {
            SolrDocstoreConnectorFactory factory = new SolrDocstoreConnectorFactory ();
            factory.getInstance ().dispatchEvent (event);
            LOGGER.info ("Accepted one event from the queue: {}", event.toString ());
        }
        catch( SolrDocstoreConnectorException solrDocstoreConnectorEvent) {
            throw new AttachmentDbEventAcceptException ("Unable to accept event", solrDocstoreConnectorEvent);
        }
    }
}
