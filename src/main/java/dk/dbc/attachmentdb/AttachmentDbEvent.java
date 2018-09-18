/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import java.util.Objects;

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
    AttachmentDbEvent(long eventId, String consumerId,
                      String bibliographicRecordId, int agencyId, boolean isActive) {
        this.id = eventId;
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

    @Override
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
