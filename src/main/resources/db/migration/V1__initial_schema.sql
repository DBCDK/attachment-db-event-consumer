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
