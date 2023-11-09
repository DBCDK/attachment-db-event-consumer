/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.commons.jdbc.util.JDBCUtil;
import dk.dbc.commons.testcontainers.postgres.DBCPostgreSQLContainer;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class IntegrationTest {
    final static DBCPostgreSQLContainer DB_CONTAINER = makeDBContainer();

    @BeforeClass
    public static void createDatabase() {
        DatabaseMigrator databaseMigrator = new DatabaseMigrator(DB_CONTAINER.datasource());
        databaseMigrator.migrate();
    }

    @Before
    public void resetDatabase() throws SQLException {
        try (Connection conn = DB_CONTAINER.createConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM attachment");
            statement.executeUpdate("DELETE FROM event");
            statement.executeUpdate("DELETE FROM consumer");
            statement.executeUpdate("ALTER SEQUENCE event_id_seq RESTART");
            executeScript(new File("src/test/resources/consumers.sql"));
        }
    }

    static void executeScript(File scriptFile) {
        try (Connection conn = DB_CONTAINER.createConnection()) {
            JDBCUtil.executeScript(conn, scriptFile, StandardCharsets.UTF_8.name());
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static DBCPostgreSQLContainer makeDBContainer() {
        DBCPostgreSQLContainer container = new DBCPostgreSQLContainer().withReuse(false);
        container.start();
        container.exposeHostPort();
        return container;
    }
}
