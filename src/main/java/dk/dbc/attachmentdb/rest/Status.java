/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb.rest;

import dk.dbc.serviceutils.ServiceStatus;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Stateless
@Path("")
public class Status implements ServiceStatus {
    @Resource(lookup = "jdbc/attachment-db")
    DataSource dataSource;

    @Override
    public Response getStatus() {
        pingAttachmentDb();
        return ServiceStatus.super.getStatus();
    }

    private void pingAttachmentDb() {
        try (Connection conn = dataSource.getConnection();
             Statement ping = conn.createStatement()) {
            ping.execute("SELECT 1");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
