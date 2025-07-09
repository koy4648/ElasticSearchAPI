package com.saltlux.kys.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.saltlux.kys.domain.ArticleES;
import com.saltlux.kys.dto.request.SearchFilterRequest;
import com.saltlux.kys.dto.response.SearchApiResponse;
import com.saltlux.kys.util.PageableUtils;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchApiResponse searchByKeywordBoolQuery(String field,
        List<String> andKeywords, List<String> orKeywords, List<String> notKeywords,
        Pageable pageable) {
        if (CollectionUtils.isEmpty(andKeywords) && CollectionUtils.isEmpty(orKeywords)
            && CollectionUtils.isEmpty(notKeywords)) {
            throw new IllegalArgumentException("최소 하나 이상의 검색 키워드를 입력해주세요.");
        }
        Query boolQuery = BoolQuery.of(b -> {
            if (!CollectionUtils.isEmpty(andKeywords)) {
                b.must(buildTermsQuery(field, andKeywords));
            }
            if (!CollectionUtils.isEmpty(orKeywords)) {
                b.should(buildTermsQuery(field, orKeywords));
                b.minimumShouldMatch("1");
            }
            if (!CollectionUtils.isEmpty(notKeywords)) {
                b.mustNot(buildTermsQuery(field, notKeywords));
            }
            return b;
        })._toQuery();
        return executeSearch(boolQuery, pageable);
    }

    public SearchApiResponse searchByMatchPhrase(String field, String phrase, int slop,
        Pageable pageable) {
        if (!StringUtils.hasText(phrase)) {
            throw new IllegalArgumentException("검색할 구문을 입력해주세요");
        }
        Query phraseQuery = QueryBuilders.matchPhrase(m -> m.field(field).query(phrase).slop(slop));
        return executeSearch(phraseQuery, pageable);
    }

    public SearchApiResponse searchByFilters(SearchFilterRequest request, Pageable pageable) {
        List<Query> filterClauses = new ArrayList<>();
        if (StringUtils.hasText(request.category())) {
            filterClauses.add(buildTermQuery("category", request.category()));
        }
        if (StringUtils.hasText(request.providerCode())) {
            filterClauses.add(buildTermQuery("providerCode", request.providerCode()));
        }
        if (StringUtils.hasText(request.person())) {
            filterClauses.add(buildTermQuery("tms_entity_name_person", request.person()));
        }
        if (StringUtils.hasText(request.organization())) {
            filterClauses.add(
                buildTermQuery("tms_entity_name_organization", request.organization()));
        }

        if (request.minScore() != null || request.maxScore() != null) {
            filterClauses.add(RangeQuery.of(r -> {
                r.field("tms_sentiment_polarity_score");
                if (request.minScore() != null) {
                    r.gte(JsonData.of(request.minScore()));
                }
                if (request.maxScore() != null) {
                    r.lte(JsonData.of(request.maxScore()));
                }
                return r;
            })._toQuery());
        }

        if (request.startDate() != null || request.endDate() != null) {
            filterClauses.add(RangeQuery.of(r -> {
                r.field("publishedAt");
                if (request.startDate() != null) {
                    r.gte(JsonData.of(request.startDate()));
                }
                if (request.endDate() != null) {
                    r.lte(JsonData.of(request.endDate()));
                }
                return r;
            })._toQuery());
        }
        Query finalQuery;
        if (filterClauses.isEmpty()) {
            finalQuery = MatchAllQuery.of(m -> m)._toQuery();
        } else {
            finalQuery = BoolQuery.of(b -> b.filter(filterClauses))._toQuery();
        }

        return executeSearch(finalQuery, pageable);
    }

    private Query buildTermQuery(String field, String value) {
        return TermQuery.of(t -> t.field(field).value(value))._toQuery();
    }

    private Query buildTermsQuery(String field, List<String> keywords) {
        List<FieldValue> fieldValues = keywords.stream().map(FieldValue::of).toList();
        return TermsQuery.of(t -> t.field(field).terms(ts -> ts.value(fieldValues)))._toQuery();
    }

    private SearchApiResponse executeSearch(Query query, Pageable pageable) {
        Pageable safePageable = PageableUtils.sanitize(pageable);
        NativeQuery searchQuery = NativeQuery.builder().withQuery(query)
            .withPageable(safePageable).build();
        SearchHits<ArticleES> hits = elasticsearchOperations.search(searchQuery,
            ArticleES.class);
        List<ArticleES> documents = hits.getSearchHits().stream().map(SearchHit::getContent)
            .collect(Collectors.toList());

        return SearchApiResponse.builder().totalHits(hits.getTotalHits()).documents(documents)
            .build();
    }
}
