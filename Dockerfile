FROM docker.dbc.dk/payara5-micro:latest

USER gfish

LABEL ATTACHMENT_DB_URL="attachment db url"
LABEL CONSUMER_ID="event consumer ID"
LABEL SOLR_DOC_STORE_URL="solr-doc store destination"

COPY target/attachment-db-event-consumer.war target/docker/attachment-db-event-consumer.json deployments/

EXPOSE 8080
