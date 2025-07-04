package com.saltlux.kys.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.saltlux.kys.domain.ArticleDataESBasic;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class SerchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<ArticleDataESBasic> searchByKeywordBasic(String field, String keyword,
        Pageable pageable) {
        String[] keywords = null;
        if (keyword == null || keyword.isEmpty()) {
            Objects.requireNonNull(keyword, "키워드는 null이 될 수 없습니다.");
        }
        Query matchQuery = QueryBuilders.match(m -> m.field(field).query(keyword));
        NativeQuery searchQuery = NativeQuery.builder().withQuery(matchQuery).withPageable(pageable)
            .build();
        return elasticsearchOperations.search(searchQuery, ArticleDataESBasic.class);

    }

    public SearchHits<ArticleDataESBasic> searchByKeywordBoolQuery(String field,
        List<String> andKeywords,
        List<String> orKeywords, List<String> notKeywords, Pageable pageable) {
        boolean andEmpty = andKeywords == null || andKeywords.isEmpty();
        boolean orEmpty = orKeywords == null || orKeywords.isEmpty();
        boolean notEmpty = notKeywords == null || notKeywords.isEmpty();

        if (andEmpty && orEmpty && notEmpty) {
            throw new IllegalArgumentException("최소 하나 이상의 검색 키워드를 입력해주세요.");
        }
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        if (!andEmpty) {
            for (String keyword : andKeywords) {
                boolQuery.must(QueryBuilders.match(m -> m.field(field).query(keyword)));
            }
        }
        if (!orEmpty) {
            for (String keyword : orKeywords) {
                boolQuery.should(QueryBuilders.match(m -> m.field(field).query(keyword)));
            }
            boolQuery.minimumShouldMatch("1");
        }
        if (!notEmpty) {
            for (String keyword : notKeywords) {
                boolQuery.mustNot(QueryBuilders.match(m -> m.field(field).query(keyword)));
            }
        }
        Query finalBoolQuery = new Query(boolQuery.build());
        NativeQuery searchQuery = new NativeQueryBuilder().withQuery(finalBoolQuery)
            .withPageable(pageable).build();

        return elasticsearchOperations.search(searchQuery, ArticleDataESBasic.class);
    }


    public SearchHits<ArticleDataESBasic> searchByMatchPhrase(String field, String phrase,
        int slop,Pageable pageable) {
        if (phrase == null || phrase.isEmpty()) {
            throw new IllegalArgumentException("검색할 구문을 입력해주세요");
        }
        Query phraseQuery = QueryBuilders.matchPhrase(m -> m.field(field).query(phrase).slop(slop));
        NativeQuery searchQuery = NativeQuery.builder().withQuery(phraseQuery).withPageable(pageable).build();

        return elasticsearchOperations.search(searchQuery, ArticleDataESBasic.class);
    }
}
