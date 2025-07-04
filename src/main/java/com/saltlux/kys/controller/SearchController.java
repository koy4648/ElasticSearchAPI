package com.saltlux.kys.controller;

import com.saltlux.kys.service.SerchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SerchService searchService;
    private final SerchService serchService;

    @GetMapping("/basic")
    public ResponseEntity<?> basicSearch(@RequestParam String field, @RequestParam String keywords,
        Pageable pageable) {
        try {
            return ResponseEntity.ok(serchService.searchByKeywordBasic(field, keywords, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/bool")
    public ResponseEntity<?> boolSearch(@RequestParam String field,
        @RequestParam(required = false) List<String> andKeywords,
        @RequestParam(required = false) List<String> orKeywords,
        @RequestParam(required = false) List<String> notKeywords, Pageable pageable) {
        try {
            return ResponseEntity.ok(
                searchService.searchByKeywordBoolQuery(field, andKeywords, orKeywords, notKeywords,
                    pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/match")
    public ResponseEntity<?> matchPhraseSearch(@RequestParam String field,
        @RequestParam String phrase, Pageable pageable) {
        try {
            return ResponseEntity.ok(
                searchService.searchByMatchPhrase(field, phrase, 0, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/proximity")
    public ResponseEntity<?> proximityPhraseSearch(@RequestParam String field,
        @RequestParam String phrase, int slop, Pageable pageable) {
        try {
            return ResponseEntity.ok(
                searchService.searchByMatchPhrase(field, phrase, slop, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
