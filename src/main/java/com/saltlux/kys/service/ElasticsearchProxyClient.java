package com.saltlux.kys.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ElasticsearchProxyClient {

    private final WebClient webClient;

    public ElasticsearchProxyClient(WebClient.Builder webClientBuilder,
        @Value("${spring.elasticsearch.uris}") String esHost) {
        this.webClient = webClientBuilder.baseUrl(esHost).build();
    }

    public String proxySearch(String index, String queryJson) {
        return webClient.post()
            .uri("/{index}/_search", index)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(queryJson)
            .retrieve()
            .onStatus(HttpStatusCode::isError,
                clientResponse -> clientResponse.bodyToMono(String.class)
                    .map(body -> new RuntimeException("Elasticsearch Error: " + body)))
            .bodyToMono(String.class)
            .block();
    }
}