package com.saltlux.kys.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import com.saltlux.kys.domain.ArticleES;
import com.saltlux.kys.util.PageableUtils;
import java.util.Map;
import org.springframework.core.annotation.MergedAnnotations.Search;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.saltlux.kys.domain.ArticleBasicInfo;
import com.saltlux.kys.dto.response.SearchApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
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

    private Query buildTermsQuery(String field, List<String> keywords) {
        List<FieldValue> fieldValues = keywords.stream().map(FieldValue::of).toList();
        return TermsQuery.of(t -> t.field(field).terms(ts -> ts.value(fieldValues)))._toQuery();
    }

    public SearchApiResponse searchByMatchPhrase(String field, String phrase, int slop,
        Pageable pageable) {
        if (!StringUtils.hasText(phrase)) {
            throw new IllegalArgumentException("검색할 구문을 입력해주세요");
        }
        Query phraseQuery = QueryBuilders.matchPhrase(m -> m.field(field).query(phrase).slop(slop));
        return executeSearch(phraseQuery, pageable);
    }

    private SearchApiResponse executeSearch(Query query, Pageable pageable) {
        Pageable safePageable = PageableUtils.sanitize(pageable);
        NativeQuery searchQuery = NativeQuery.builder().withQuery(query).withPageable(safePageable)
            .withPageable(pageable).build();
        SearchHits<ArticleES> hits = elasticsearchOperations.search(searchQuery, ArticleES.class);
        List<ArticleES> documents = hits.getSearchHits().stream().map(SearchHit::getContent)
            .collect(Collectors.toList());

        return SearchApiResponse.builder().totalHits(hits.getTotalHits()).documents(documents)
            .build();
    }
}
