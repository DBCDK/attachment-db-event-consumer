FROM docker.dbc.dk/dbc-payara-micro-logback:4

USER gfish

LABEL ATTACHMENT_DB_URL="attachment db url"
LABEL CONSUMER_ID="event consumer ID"
LABEL SOLR_DOC_STORE_URL="solr-doc store destination"

COPY target/attachment-db-event-consumer.war wars
COPY target/docker /payara-micro/config.d

EXPOSE 8080
