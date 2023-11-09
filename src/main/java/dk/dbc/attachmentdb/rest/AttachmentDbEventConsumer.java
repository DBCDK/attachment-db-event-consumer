/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class AttachmentDbEventConsumer extends Application {
    private static final Set<Class<?>> classes = new HashSet<>();
    static {
        classes.add(Status.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
