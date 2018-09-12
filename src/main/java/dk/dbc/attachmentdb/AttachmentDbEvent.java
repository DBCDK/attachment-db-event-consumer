/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "event")
@NamedQueries({
        @NamedQuery(
                name = AttachmentDbEvent.REMOVE_EVENT_QUERY_NAME,
                query = AttachmentDbEvent.REMOVE_EVENT_QUERY)
})
public class AttachmentDbEvent {
    public static final String REMOVE_EVENT_QUERY =
            "SELECT FUNCTION('remove_event', :consumerId)" +
            " FROM AttachmentDbEvent event";
    public static final String REMOVE_EVENT_QUERY_NAME =
            "AttachmentDbEvent.removeEvent";

    public AttachmentDbEvent() { }

    AttachmentDbEvent(String consumerId, String bibliographicRecordId, int agencyId, boolean isActive) {
        this.id = -1;
        this.consumerId = consumerId;
        this.bibliographicRecordId = bibliographicRecordId;
        this.agencyId = agencyId;
        this.isActive = isActive;
    }

    @Id
    @GeneratedValue
    private long id;

    @JsonIgnore
    public long getId() {
        return id;
    }

    private String consumerId;

    public String getConsumerId() {
        return consumerId;
    }

    private String bibliographicRecordId;

    public String getBibliographicRecordId() {
        return bibliographicRecordId;
    }

    private int agencyId;

    public int getAgencyId() {
        return agencyId;
    }

    private boolean isActive;

    @JsonProperty("value")
    public boolean IsActive() {
        return isActive;
    }
}
