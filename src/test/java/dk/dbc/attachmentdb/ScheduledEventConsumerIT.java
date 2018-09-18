/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import org.eclipse.microprofile.metrics.Counter;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ScheduledEventConsumerIT extends JpaIntegrationTest {
    private final SolrDocstoreConnector solrDocstoreConnector = mock(SolrDocstoreConnector.class);

    @Test
    public void consumeEvents() {
        final AttachmentDbEvent aRec1Insert = new AttachmentDbEvent(
                1, "consumer_a", "Rec1", 870970, true);
        final AttachmentDbEvent bRec1Insert = new AttachmentDbEvent(
                2, "consumer_b", "Rec1", 870970, true);
        final AttachmentDbEvent aRec1Delete = new AttachmentDbEvent(
                3, "consumer_a", "Rec1", 870970, false);
        inTransaction(() -> {
            entityManager.persist(aRec1Insert);
            entityManager.persist(bRec1Insert);
            entityManager.persist(aRec1Delete);
        });

        final ScheduledEventConsumer scheduledEventConsumer =
                createScheduledEventConsumer("consumer_a");

        inTransaction(scheduledEventConsumer::run);

        assertThat("number of events consumed",
                scheduledEventConsumer.eventCounter.getCount(), is(2L));

        final List<AttachmentDbEvent> remainingEvents = entityManager.createQuery(
                "SELECT event FROM AttachmentDbEvent event", AttachmentDbEvent.class)
                .getResultList();
        assertThat("number of remaining events", remainingEvents.size(), is(1));
        assertThat("remaining event", remainingEvents.get(0), is(bRec1Insert));

        //final InOrder solrDocstoreConnectorVerifier = Mockito.inOrder(solrDocstoreConnector);
        //solrDocstoreConnectorVerifier.verify(solrDocstoreConnector).dispatchEvent(aRec1Insert);
        //solrDocstoreConnectorVerifier.verify(solrDocstoreConnector).dispatchEvent(aRec1Delete);
    }

    private ScheduledEventConsumer createScheduledEventConsumer(String consumerId) {
        final EventConsumer eventConsumer = new EventConsumer();
        eventConsumer.consumerId = consumerId;
        eventConsumer.entityManager = entityManager;
        eventConsumer.solrDocstoreConnector = solrDocstoreConnector;
        final ScheduledEventConsumer scheduledEventConsumer = new ScheduledEventConsumer();
        scheduledEventConsumer.eventConsumer = eventConsumer;
        scheduledEventConsumer.eventCounter = new Counter() {
            long count = 0;
            @Override
            public void inc() {
                count++;
            }

            @Override
            public void inc(long l) {
                count = count + l;
            }

            @Override
            public void dec() {
                count--;
            }

            @Override
            public void dec(long l) {
                count = count - l;
            }

            @Override
            public long getCount() {
                return count;
            }
        };
        return scheduledEventConsumer;
    }
}
