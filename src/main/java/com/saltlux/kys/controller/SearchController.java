package com.saltlux.kys.controller;

import com.saltlux.kys.service.ElasticsearchProxyClient;
import com.saltlux.kys.dto.request.SearchFilterRequest;
import com.saltlux.kys.util.PageableUtils;
import com.saltlux.kys.dto.response.SearchApiResponse;
import com.saltlux.kys.service.SearchService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ElasticsearchProxyClient esProxyClient;
    private final SearchService searchService;


    @GetMapping("/bool")
    public ResponseEntity<SearchApiResponse> boolSearch(@RequestParam String field,
        @RequestParam(required = false) List<String> andKeywords,
        @RequestParam(required = false) List<String> orKeywords,
        @RequestParam(required = false) List<String> notKeywords, Pageable pageable) {
        SearchApiResponse searchApiResponse = searchService.searchByKeywordBoolQuery(field,
            andKeywords, orKeywords, notKeywords, pageable);
        return ResponseEntity.ok(searchApiResponse);
    }

    @GetMapping("/match-phrase")
    public ResponseEntity<SearchApiResponse> matchPhraseSearch(@RequestParam String field,
        @RequestParam String phrase, @RequestParam(defaultValue = "0") int slop,
        Pageable pageable) {
        Pageable safePageable = PageableUtils.sanitize(pageable);
        SearchApiResponse searchApiResponse = searchService.searchByMatchPhrase(field, phrase, slop,
            safePageable);
        return ResponseEntity.ok(searchApiResponse
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<SearchApiResponse> filterSearch(
        @ParameterObject SearchFilterRequest request,
        @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(searchService.searchByFilters(request, pageable));
    }

    @PostMapping("/proxy")
    public ResponseEntity<String> proxySearch(
        @RequestParam(defaultValue = "article") String index,
        @RequestBody String queryJson
    ) {
        // ✅ 컨트롤러는 이제 매우 깔끔하게 클라이언트 호출만 담당합니다.
        String response = esProxyClient.proxySearch(index, queryJson);
        return ResponseEntity.ok(response);
    }
}
