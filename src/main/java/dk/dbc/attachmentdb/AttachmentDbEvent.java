package dk.dbc.attachmentdb;

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
    private long id;

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

    public boolean getIsActive() {
        return isActive;
    }
}
