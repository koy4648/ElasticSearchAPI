package com.saltlux.kys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.kys.domain.ArticleDataES;
import com.saltlux.kys.domain.ArticleDataMongo;
import com.saltlux.kys.repository.ArticleEsRepository;
import com.saltlux.kys.repository.ArticleMongoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexingService {

    private final ArticleMongoRepository mongoRepository;
    private final ArticleEsRepository esRepository;
    private final ObjectMapper objectMapper;

    public void parseJson() throws IOException {
        Path path = Paths.get("./dataExample.txt");
        List<ArticleDataMongo> articles = Files.lines(path).map(text -> {
            try {
                return objectMapper.readValue(text, ArticleDataMongo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        mongoRepository.saveAll(articles);
    }

    public void indexToEs() {
        List<ArticleDataMongo> articleDataMongo = mongoRepository.findAll();
        List<ArticleDataES> articleDataES = articleDataMongo.stream()
            .map(this::convertToArticleDocument).toList();
        esRepository.saveAll(articleDataES);
    }


    private ArticleDataES convertToArticleDocument(ArticleDataMongo articleDataMongo) {

        return new ArticleDataES(articleDataMongo.getNewsId(),
            articleDataMongo.getPublishedAt(), articleDataMongo.getCategory(),
            articleDataMongo.getTitle(),
            articleDataMongo.getContent(), articleDataMongo.getByline(),
            articleDataMongo.getProviderCode());
    }

}
