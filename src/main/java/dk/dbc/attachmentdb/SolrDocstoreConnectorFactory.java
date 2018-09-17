/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.attachmentdb;

import dk.dbc.httpclient.HttpClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;

@ApplicationScoped
public class SolrDocstoreConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrDocstoreConnectorFactory.class);

    public static SolrDocstoreConnector create(String solrDocstoreServiceBaseUrl) {
        final Client client = HttpClient.newClient(new ClientConfig()
                .register(new JacksonFeature()));
        LOGGER.info("Creating SolrDocstoreServiceConnector for: {}", solrDocstoreServiceBaseUrl);
        return new SolrDocstoreConnector(client, solrDocstoreServiceBaseUrl);
    }

    @Inject
    @ConfigProperty(name = "SOLR_DOC_STORE_URL")
    private String solrDocstoreServiceBaseUrl;

    SolrDocstoreConnector solrDocstoreConnector;

    @PostConstruct
    public void initializeConnector() {
        solrDocstoreConnector = SolrDocstoreConnectorFactory.create(solrDocstoreServiceBaseUrl);
    }

    @Produces
    public SolrDocstoreConnector getInstance() {
        return solrDocstoreConnector;
    }

    @PreDestroy
    public void tearDownConnector() {
        solrDocstoreConnector.close();
    }
}
