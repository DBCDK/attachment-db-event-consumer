/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.commons.jdbc.util.JDBCUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class IntegrationTest {
    static final PGSimpleDataSource datasource;

    static {
        datasource = new PGSimpleDataSource();
        datasource.setDatabaseName("attachment_db");
        datasource.setServerName("localhost");
        datasource.setPortNumber(Integer.parseInt(
                System.getProperty("postgresql.port", "5432")));
        datasource.setUser(System.getProperty("user.name"));
        datasource.setPassword(System.getProperty("user.name"));
    }

    @BeforeClass
    public static void createDatabase() {
        final DatabaseMigrator databaseMigrator = new DatabaseMigrator(datasource);
        databaseMigrator.migrate();
    }

    @Before
    public void resetDatabase() throws SQLException {
        try (Connection conn = datasource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM attachment");
            statement.executeUpdate("DELETE FROM event");
            statement.executeUpdate("DELETE FROM consumer");
            statement.executeUpdate("ALTER SEQUENCE event_id_seq RESTART");
            executeScript(new File("src/test/resources/consumers.sql"));
        }
    }

    static void executeScript(File scriptFile) {
        try (Connection conn = datasource.getConnection()) {
            JDBCUtil.executeScript(conn, scriptFile, StandardCharsets.UTF_8.name());
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
