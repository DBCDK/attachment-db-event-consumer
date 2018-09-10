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

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonProperty("consumerId")
    private String consumerId;

    @JsonIgnore
    public String getConsumerId() {
        return consumerId;
    }

    @JsonProperty("bibliographicRecordId")
    private String bibliographicRecordId;

    @JsonIgnore
    public String getBibliographicRecordId() {
        return bibliographicRecordId;
    }

    @JsonProperty("agencyId")
    private int agencyId;

    @JsonIgnore
    public int getAgencyId() {
        return agencyId;
    }

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonIgnore
    public boolean getIsActive() {
        return isActive;
    }
}
