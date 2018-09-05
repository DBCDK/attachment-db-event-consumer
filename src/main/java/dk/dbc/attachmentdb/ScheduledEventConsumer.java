package dk.dbc.attachmentdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    public void run() {
        try {

            // Todo:
            // consume()
            //   - poll()
            //   - acceptEvent()

        } catch (Exception e) {
            LOGGER.error("Exception caught while processing events", e);
        }
    }
}
