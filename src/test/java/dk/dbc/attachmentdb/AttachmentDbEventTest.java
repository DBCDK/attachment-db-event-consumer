/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class AttachmentDbEventTest {

    @Test
    public void serialize_event() {
        AttachmentDbEvent event = new AttachmentDbEvent(
                1, "test", "123456", 999999, true);

        try {
            JSONBContext context = new JSONBContext();
            String json = context.marshall(event);
            assertEquals("{\"consumerId\":\"test\",\"bibliographicRecordId\":\"123456\",\"agencyId\":999999,\"value\":true}", json);
        } catch(JSONBException e) {
            fail(String.format(e.getMessage()));
        }
    }
}
