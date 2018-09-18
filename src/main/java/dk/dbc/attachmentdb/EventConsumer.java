/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.util.Timed;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This bean consumes events from the attachment queue and
 * ships them off to the SolrDocstore service for processing.
 */
@Stateless
public class EventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @PersistenceContext(unitName = "attachmentdb_PU")
    EntityManager entityManager;

    @Inject
    @ConfigProperty(name = "CONSUMER_ID")
    String consumerId;

    @Inject
    SolrDocstoreConnector solrDocstoreConnector;

    /**
     * Consume next event from the attachment events queue.
     * On error, the event queue is rolled back.
     * @return Number of events consumed
     * @throws AttachmentDbEventAcceptException on failure to publish event
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int consume() throws AttachmentDbEventAcceptException {
        AttachmentDbEvent event = poll ();
        if (event != null) {
            LOGGER.info ("Received one event from the queue: {}", event.toString ());
            accept (event);
            return 1;
        }
        return 0;
    }

    /**
     * Poll the attachment queue and return next event
     * @return AttachmentDbEvent if there is new events, otherwise null
     */
    @Timed
    public AttachmentDbEvent poll() {
        final List<AttachmentDbEvent> events = entityManager
                .createNamedQuery(AttachmentDbEvent.REMOVE_EVENT_QUERY_NAME, AttachmentDbEvent.class)
                .setParameter("consumerId", consumerId)
                .getResultList();
        return events.isEmpty () ? null : events.get(0);
    }

    /**
     * Publishes event to Solr doc-store
     * @param event event to be published
     * @throws AttachmentDbEventAcceptException if the event could not be
     * delivered to the Solr doc-store
     */
    @Timed
    public void accept(AttachmentDbEvent event) throws AttachmentDbEventAcceptException {
        try {
            // disabled until solr doc-store endpoint exists
            //solrDocstoreConnector.dispatchEvent (event);
            LOGGER.info ("Accepted one event from the queue: {}", event.toString ());
        }
        //catch(SolrDocstoreConnectorException e) {
        catch (RuntimeException e) {
            throw new AttachmentDbEventAcceptException ("Unable to accept event", e);
        }
    }
}
