/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
@Table(name = "event")
@NamedNativeQueries({
    @NamedNativeQuery(name = AttachmentDbEvent.REMOVE_EVENT_QUERY_NAME,
        query = "SELECT * FROM remove_event(?consumerId)",
        resultClass = AttachmentDbEvent.class)
})
public class AttachmentDbEvent {
    public static final String REMOVE_EVENT_QUERY_NAME =
            "AttachmentDbEvent.removeEvent";

    public AttachmentDbEvent() { }

    // For testing purposes only
    AttachmentDbEvent(long eventId, String consumerId, String bibliographicRecordId, int agencyId, boolean isActive) {
        id = eventId;
        this.consumerId = consumerId;
        this.bibliographicRecordId = bibliographicRecordId;
        this.agencyId = agencyId;
        this.isActive = isActive;
    }

    @Id
    private long id;

    @JsonIgnore
    public long getId() {
        return id;
    }

    private String consumerId;

    @JsonIgnore
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
    public boolean isActive() {
        return isActive;
    }

    @Transient
    @JsonProperty("field")
    public String getType() {
        return "hasCoverUrl";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttachmentDbEvent that = (AttachmentDbEvent) o;
        return id == that.id &&
                agencyId == that.agencyId &&
                isActive == that.isActive &&
                Objects.equals(consumerId, that.consumerId) &&
                Objects.equals(bibliographicRecordId, that.bibliographicRecordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, consumerId, bibliographicRecordId, agencyId, isActive);
    }

    @Override
    public String toString () {
        return "AttachmentDbEvent{" +
                "id=" + id +
                ", consumerId='" + consumerId + '\'' +
                ", bibliographicRecordId='" + bibliographicRecordId + '\'' +
                ", agencyId=" + agencyId +
                ", isActive=" + isActive +
                '}';
    }
}
