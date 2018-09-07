/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb.rest;

import dk.dbc.serviceutils.ServiceStatus;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
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
