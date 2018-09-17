/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * This enterprise Java bean represents attempts at dispatching requests to SolrDocStore,
 * for updating the index that indicates, if a cover image is available for a given faust+agency.
 * The update is triggered by new covers being added to, or existing covers removed from
 * the attachments database in MoreInfo.
 */
@Singleton
@Startup
public class ScheduledEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledEventConsumer.class);

    // ToDo: Consider if we should make this a configurable property?
    private final static int MAX_EVENTS_CONSUMED = 1000;

    @EJB EventConsumer eventConsumer;

    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    public void run() {
        try {
            LOGGER.info ("Consume new events from attachment-db");

            int consumed = 0;
            while (consumed < MAX_EVENTS_CONSUMED && eventConsumer.consume() > 0) {
                consumed++;
            }
            LOGGER.info ("Consumed {} new events from attachment-db", consumed);
        } catch (Exception e) {
            LOGGER.error("Exception caught while processing events", e);
        }
    }
}
