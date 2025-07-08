package com.saltlux.kys.controller;

import com.saltlux.kys.domain.ArticleES;
import com.saltlux.kys.util.PageableUtils;
import org.springframework.data.elasticsearch.core.SearchHit;
import com.saltlux.kys.domain.ArticleBasicInfo;
import com.saltlux.kys.dto.response.SearchApiResponse;
import com.saltlux.kys.service.SearchService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    //    @GetMapping("/test")
//    public ResponseEntity<SearchApiResponse> testSearch() {
//        SearchHits<ArticleBasicInfo> searchHits = searchService.searchTest();
//
//        List<ArticleBasicInfo> articles = searchHits.getSearchHits()
//            .stream()
//            .map(SearchHit::getContent)
//            .collect(Collectors.toList());
//
//        SearchApiResponse apiResponse;// = new SearchApiResponse(searchHits.getTotalHits(), articles);
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @GetMapping("/basic")
//    public ResponseEntity<?> basicSearch(@RequestParam String field, @RequestParam String keywords,
//        Pageable pageable) {
//        Pageable safePageable = PageableUtils.sanitize(pageable);
//        return ResponseEntity.ok(
//            searchService.searchByKeywordBasic(field, keywords, safePageable));
//    }
//
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

}
