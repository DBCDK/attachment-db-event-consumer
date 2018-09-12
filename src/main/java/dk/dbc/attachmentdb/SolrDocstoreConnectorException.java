/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

public class SolrDocstoreConnectorException extends Exception {

    /**
     * Constructs a new exception with the specified detail message
     *
     * @param message detail message saved for later retrieval by the
     *                {@link #getMessage()} method. May be null.
     */
    public SolrDocstoreConnectorException(String message) {
        super(message);
    }
}
