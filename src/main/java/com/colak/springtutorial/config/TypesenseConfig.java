package com.colak.springtutorial.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.typesense.api.Client;
import org.typesense.api.Configuration;
import org.typesense.resources.Node;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.context.annotation.Configuration
public class TypesenseConfig {
    @Value("${typesense.api-key}")
    private String apiKey;

    @Value("${typesense.host}")
    private String host;

    @Value("${typesense.port}")
    private String port;

    @Value("${typesense.protocol}")
    private String protocol;

    @Value("${typesense.connection-timeout-seconds}")
    private int connectionTimeoutSeconds;

    @Bean
    public Client typesenseClient() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node(protocol, host, port));
        Configuration configuration = new Configuration(
                nodes,
                Duration.ofSeconds(connectionTimeoutSeconds),
                apiKey
        );

        Client client = new Client(configuration);

        try {
            client.health.retrieve();
        } catch (Exception e) {
            log.error("Failed to connect to Typesense server", e);
        }

        return client;
    }


}
