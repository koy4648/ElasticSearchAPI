package com.saltlux.kys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.kys.domain.ArticleES;
import com.saltlux.kys.domain.ArticleBasicInfo;
import com.saltlux.kys.domain.ArticleMongo;
import com.saltlux.kys.dto.request.TMSAnalysisRequest;
import com.saltlux.kys.dto.response.TmsAnalysisResponse;
import com.saltlux.kys.repository.ArticleMongoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexingService {

    private final WebClient webClient;
    private final ArticleMongoRepository mongoRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;
    @Value("${indexing.source-file-path}")
    private String sourceFilePath;

    public void parseJson() throws IOException {
        Path path = Paths.get(sourceFilePath);
        List<ArticleMongo> articles = Files.lines(path).map(text -> {
            try {
                return objectMapper.readValue(text, ArticleMongo.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse JSON line: {}", text, e);
                return null;
            }
        }).filter(Objects::nonNull).toList();

        mongoRepository.saveAll(articles);
    }

    public void indexBasicToEs() {
        List<ArticleMongo> articleMongos = mongoRepository.findAll();
        List<ArticleBasicInfo> articleDataEBasics = articleMongos.stream()
            .map(this::convertBasicToIndex).toList();
        elasticsearchOperations.save(articleDataEBasics);
    }

    private ArticleBasicInfo convertBasicToIndex(ArticleMongo articleMongo) {
        return new ArticleBasicInfo(articleMongo.getNewsId(),
            Instant.parse(articleMongo.getPublishedAt()), articleMongo.getCategory(),
            articleMongo.getTitle(),
            articleMongo.getContent(), articleMongo.getByline(),
            articleMongo.getProviderCode());
    }

    public void indexArticleToEs() {
        List<ArticleMongo> articleMongos = mongoRepository.findAll();
        List<ArticleES> articleData = Flux.fromIterable(articleMongos)
            .flatMap(this::convertArticleToIndex).collectList().block();
        if (articleData != null && !articleData.isEmpty()) {
            elasticsearchOperations.save(articleData);
        }
    }

    private Mono<ArticleES> convertArticleToIndex(ArticleMongo articleMongo) {
        String toConvert = articleMongo.getTitle().concat(articleMongo.getContent());

        return getTMSResult(toConvert).map(
            tmsAnalysisResponse -> new ArticleES(articleMongo.getNewsId(),
                Instant.parse(articleMongo.getPublishedAt()), articleMongo.getCategory(),
                articleMongo.getTitle(),
                articleMongo.getContent(), articleMongo.getByline(),
                articleMongo.getProviderCode(),
                tmsAnalysisResponse.getTmsSentimentNegativeText(),
                tmsAnalysisResponse.getTmsSentimentPositiveText(),
                tmsAnalysisResponse.getTmsBigram(),
                tmsAnalysisResponse.getTmsFeature(),
                tmsAnalysisResponse.getTmsEntityNameLocation(),
                tmsAnalysisResponse.getTmsSentimentPolarityScore(),
                tmsAnalysisResponse.getTmsRawStreamIndex(),
                tmsAnalysisResponse.getTmsRawStreamStore(),
                tmsAnalysisResponse.getTmsEntityNamePerson(),
                tmsAnalysisResponse.getTmsMorph(),
                tmsAnalysisResponse.getTmsEntityNameOrganization()
            ));
    }

    private Mono<TmsAnalysisResponse> getTMSResult(String text) {
        TMSAnalysisRequest request = new TMSAnalysisRequest(text);
        String TMS_API_URI = "/api/tms/analyze";
        return webClient.post().uri(TMS_API_URI).bodyValue(request)
            .retrieve().bodyToMono(TmsAnalysisResponse.class)
            .doOnError(error -> log.error("TMS 연동 실패", error))
            .onErrorReturn(new TmsAnalysisResponse());

    }
}
