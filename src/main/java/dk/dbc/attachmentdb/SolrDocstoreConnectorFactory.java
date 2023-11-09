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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;

@ApplicationScoped
public class SolrDocstoreConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrDocstoreConnectorFactory.class);

    public static SolrDocstoreConnector create(String solrDocstoreServiceBaseUrl) {
        Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
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
