package com.saltlux.kys.service;

import com.saltlux.kys.dto.request.TMSAnalysisRequest;
import com.saltlux.kys.dto.response.TmsAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TmsApiClient {

    private final WebClient webClient;

    public TmsApiClient(WebClient.Builder webClientBuilder,
        @Value("${tms.api.baseUrl}") String tmsApiBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(tmsApiBaseUrl).build();
    }

    public Mono<TmsAnalysisResponse> analyzeText(String text) {
        TMSAnalysisRequest request = new TMSAnalysisRequest(text);
        return webClient.post()
            .uri("/api/tms/analyze")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(TmsAnalysisResponse.class)
            .doOnError(error -> log.error("TMS API 호출 실패", error))
            .onErrorReturn(new TmsAnalysisResponse());
    }
}