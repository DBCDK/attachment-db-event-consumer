/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.httpclient.HttpPost;
import dk.dbc.invariant.InvariantUtil;
import dk.dbc.util.Stopwatch;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * SolrDocstoreConnector - SolrDocstore service client
 * <p>
 * To use this class, you construct an instance, specifying a web resources client as well as
 * a base URL for the docstore service endpoint you will be communicating with.
 * </p>
 * <p>
 * This class is thread safe, as long as the given web resources client remains thread safe.
 * </p>
 * <p>
 * Not all endpoints are stubbed, only those needed by this project.
 * </p>
 * <p>
 * Service home: Todo (add when known)
 * </p>
 */
public class SolrDocstoreConnector implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrDocstoreConnector.class);

    private static final String PATH_DISPATCH_EVENT = "/api/resource/add";
    private static final Set<Integer> RETRY_CODES = Set.of(404, 500, 502);

    private static final RetryPolicy<Response> RETRY_POLICY = new RetryPolicy<Response>()
            .handle(ProcessingException.class)
            .handleResultIf(response -> RETRY_CODES.contains(response.getStatus()))
            .withDelay(Duration.ofSeconds(10))
            .withMaxRetries(6);

    private final FailSafeHttpClient failSafeHttpClient;
    private final String baseUrl;

    /**
     * Returns new instance with default retry policy
     * @param httpClient web resources client
     * @param baseUrl base URL for solrdocstore service endpoint
     */
    public SolrDocstoreConnector(Client httpClient, String baseUrl) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl);
    }

    /**
     * Returns new instance with custom retry policy
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl base URL for solrdocstore service endpoint
     */
    public SolrDocstoreConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl) {
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(baseUrl, "baseUrl");
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    /**
     * Sends event to the Solr doc-store
     * @param event Event
     * @throws SolrDocstoreConnectorException on failure to communicate with the Solrdocstore service
     * @throws SolrDocstoreConnectoreUnexpectedStatusCodeException on unexpected response status code
     */
    public void addEvent(AttachmentDbEvent event) throws SolrDocstoreConnectorException {
        Stopwatch stopwatch = new Stopwatch();
        if( event == null ) {
            throw new SolrDocstoreConnectorException ("event is null");
        }
        try {
            HttpPost solrDocstorePostRequest = new HttpPost(failSafeHttpClient)
                    .withBaseUrl(baseUrl)
                    .withPathElements(PATH_DISPATCH_EVENT)
                    .withData (event, MediaType.APPLICATION_JSON);

            Response response = solrDocstorePostRequest.execute();
            assertResponseStatus(response, Response.Status.OK); // ToDo: May return CREATED instead ?, check with spec.
        } finally {
            LOGGER.info("dispatchEvent(agency:{}, record-id:{}) took {} milliseconds",
                    event.getAgencyId (), event.getBibliographicRecordId (),
                    stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * Verifies that we received the expected status code from the service
     * @param response Service response
     * @param expectedStatus The expected status
     */
    private void assertResponseStatus(Response response, Response.Status expectedStatus) throws SolrDocstoreConnectoreUnexpectedStatusCodeException {
        Response.Status actualStatus = Response.Status.fromStatusCode(response.getStatus());
        if (actualStatus != expectedStatus) {
            throw new SolrDocstoreConnectoreUnexpectedStatusCodeException(
                    String.format("Solrdocstore service returned with unexpected status code: %s", actualStatus),
                    actualStatus.getStatusCode());
        }
    }
}
