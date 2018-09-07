/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import net.jodah.failsafe.RetryPolicy;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.invariant.InvariantUtil;

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
public class SolrDocstoreConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrDocstoreConnector.class);

    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(Collections.singletonList(ProcessingException.class))
            .retryIf((Response response) -> response.getStatus() == 404
                    || response.getStatus() == 500
                    || response.getStatus() == 502)
            .withDelay(10, TimeUnit.SECONDS)
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
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(
                failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(
                baseUrl, "baseUrl");
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    // Todo: Add endpoint stubs
}
