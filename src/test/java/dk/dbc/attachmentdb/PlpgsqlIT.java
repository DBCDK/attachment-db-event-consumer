/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.commons.jdbc.util.JDBCUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlpgsqlIT extends IntegrationTest {
    @Test
    public void add_event() throws SQLException {
        try (Connection conn = datasource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.execute("SELECT * FROM add_event('rec1', 870970, true, 'consumer_a')");   // add
            statement.execute("SELECT * FROM add_event('rec1', 870970, true, 'consumer_b')");   // add
            statement.execute("SELECT * FROM add_event('rec1', 870970, true, 'consumer_a')");   // ignore
            statement.execute("SELECT * FROM add_event('rec1', 870970, false, 'consumer_a')");  // add
            statement.execute("SELECT * FROM add_event('rec1', 870970, false, 'consumer_a')");  // ignore
            assertThat("number of events",
                    JDBCUtil.getFirstInt(conn, "SELECT COUNT(*) FROM event"), is(3));
            assertEvent(1, "rec1", 870970, true, "consumer_a",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM event WHERE id = 1").get(0));
            assertEvent(2, "rec1", 870970, true, "consumer_b",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM event WHERE id = 2").get(0));
            assertEvent(3, "rec1", 870970, false, "consumer_a",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM event WHERE id = 3").get(0));
        }
    }

    @Test
    public void remove_event() throws SQLException {
        try (Connection conn = datasource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.execute("SELECT * FROM add_event('rec1', 870970, true, 'consumer_a')");
            statement.execute("SELECT * FROM add_event('rec1', 870970, true, 'consumer_b')");
            statement.execute("SELECT * FROM add_event('rec1', 870970, false, 'consumer_a')");
            assertEvent(1, "rec1", 870970, true, "consumer_a",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_a')").get(0));
            assertEvent(3, "rec1", 870970, false, "consumer_a",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_a')").get(0));
            assertEvent(2, "rec1", 870970, true, "consumer_b",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_b')").get(0));
        }
    }

    @Test
    public void attachment_insert_trigger() throws SQLException {
        try (Connection conn = datasource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("INSERT INTO attachment(lokalid,bibliotek,attachment_type) " +
                    "VALUES ('rec1', '870970', 'bagside_500')");    // ignore (not forside_*)
            assertThat("number of events",
                    JDBCUtil.getFirstInt(conn, "SELECT COUNT(*) FROM event"), is(0));
            statement.executeUpdate("INSERT INTO attachment(lokalid,bibliotek,attachment_type) " +
                    "VALUES ('rec1', '870970', 'forside_500')");    // add
            assertThat("number of events",
                    JDBCUtil.getFirstInt(conn, "SELECT COUNT(*) FROM event"), is(2));
            assertEvent(1, "rec1", 870970, true, "consumer_a",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_a')").get(0));
            assertEvent(2, "rec1", 870970, true, "consumer_b",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_b')").get(0));
            statement.executeUpdate("INSERT INTO attachment(lokalid,bibliotek,attachment_type) " +
                    "VALUES ('rec1', '870970', 'forside_10')");    // no new events added
            assertThat("number of events",
                    JDBCUtil.getFirstInt(conn, "SELECT COUNT(*) FROM event"), is(2));
        }
    }

    @Test
    public void attachment_delete_trigger() throws SQLException {
        try (Connection conn = datasource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("INSERT INTO attachment(lokalid,bibliotek,attachment_type) " +
                    "VALUES ('rec1', '870970', 'forside_500')");
            statement.executeUpdate("INSERT INTO attachment(lokalid,bibliotek,attachment_type) " +
                    "VALUES ('rec1', '870970', 'forside_10')");

            assertEvent(1, "rec1", 870970, true, "consumer_a",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_a')").get(0));
            assertEvent(2, "rec1", 870970, true, "consumer_b",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_b')").get(0));

            statement.executeUpdate("DELETE FROM attachment " +
                    "WHERE lokalid = 'rec1' AND bibliotek = '870970' AND attachment_type = 'forside_500'");

            assertThat("number of events after first delete",
                    JDBCUtil.getFirstInt(conn, "SELECT COUNT(*) FROM event"), is(0));

            statement.executeUpdate("DELETE FROM attachment " +
                    "WHERE lokalid = 'rec1' AND bibliotek = '870970' AND attachment_type = 'forside_10'");

            assertThat("number of events after last delete",
                    JDBCUtil.getFirstInt(conn, "SELECT COUNT(*) FROM event"), is(2));
            assertEvent(3, "rec1", 870970, false, "consumer_a",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_a')").get(0));
            assertEvent(4, "rec1", 870970, false, "consumer_b",
                    JDBCUtil.queryForRowMaps(conn, "SELECT * FROM remove_event('consumer_b')").get(0));
        }
    }

    private void assertEvent(int id, String bibliographicRecordId, int agencyId,
                             boolean isActive, String consumerId, Map<String, Object> row) {
        final String prefix = "event(" + id + ") ";
        assertThat(prefix + "bibliographicRecordId",
                row.get("bibliographicrecordid"), is(bibliographicRecordId));
        assertThat(prefix + "agencyId",
                row.get("agencyid"), is(agencyId));
        assertThat(prefix + "isActive",
                row.get("isactive"), is(isActive));
        assertThat(prefix + "consumerId",
                row.get("consumerid"), is(consumerId));
    }
}
