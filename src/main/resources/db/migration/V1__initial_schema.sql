CREATE TABLE consumer (
    id  TEXT PRIMARY KEY
);

CREATE TABLE event (
    id                      BIGSERIAL PRIMARY KEY,
    consumerId              TEXT NOT NULL REFERENCES consumer(id),
    bibliographicRecordId   TEXT NOT NULL,
    agencyId                INTEGER NOT NULL,
    isActive                BOOLEAN NOT NULL
);
CREATE INDEX event_idx ON event(bibliographicRecordId, agencyId, consumerId);

-- This table should already exists in production environments.
-- Only create in "from-scratch" test scenarios.
CREATE TABLE IF NOT EXISTS attachment (
    lokalid           VARCHAR(25) NOT NULL,
    bibliotek         VARCHAR(25) NOT NULL,
    attachment_type   VARCHAR(20) NOT NULL,
    ajourdato         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    opretdato         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    data              BYTEA,
    source_id         INTEGER
);

CREATE OR REPLACE FUNCTION add_event(
  bibliographicRecordId_ TEXT,
  agencyId_              INTEGER,
  isActive_              BOOLEAN,
  consumerId_            TEXT)
  RETURNS void AS $$
DECLARE
  lastEvent event;
BEGIN

  BEGIN
    SELECT *
    INTO lastEvent
    FROM event
    WHERE bibliographicRecordId = bibliographicRecordId_ AND agencyId = agencyId_ AND consumerId = consumerId_
    ORDER BY id DESC LIMIT 1;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        lastEvent := NULL;
  END;

  CASE
    WHEN lastEvent IS NULL
    THEN
      -- No existing event matches (bibliographicRecordId, agencyId, consumerId)
      INSERT INTO event(bibliographicRecordId, agencyId, consumerId, isActive)
      VALUES (bibliographicRecordId_, agencyId_, consumerId_, isActive_);
    ELSE
      IF lastEvent.isActive != isActive_ THEN
        -- Only insert if new event causes a state change
        -- when compared to the last event
        INSERT INTO event(bibliographicRecordId, agencyId, consumerId, isActive)
        VALUES (bibliographicRecordId_, agencyId_, consumerId_, isActive_);
      END IF;
  END CASE;

  RETURN;
END
$$
LANGUAGE plpgsql;
