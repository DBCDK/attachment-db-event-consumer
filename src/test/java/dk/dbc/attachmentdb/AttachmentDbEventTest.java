/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static junit.framework.TestCase.fail;

public class AttachmentDbEventTest {

    @Test
    public void serialize_event() {
        AttachmentDbEvent event = new AttachmentDbEvent(
                1, "test", "123456", 999999, true);

        try {
            JSONBContext context = new JSONBContext();
            String json = context.marshall(event);
            JSONAssert.assertEquals("{\"bibliographicRecordId\":\"123456\",\"agencyId\":999999,\"value\":true,\"field\":\"hasCoverUrl\"}",
                    json, JSONCompareMode.STRICT);
        } catch(JSONBException | JSONException e) {
            fail(String.format(e.getMessage()));
        }
    }
}
