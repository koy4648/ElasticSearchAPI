package com.saltlux.kys.controller;

import com.saltlux.kys.service.IndexingService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/index")
public class IndexingController {

    private final IndexingService indexingService;

    @PostMapping("/fileToMongo")
    public ResponseEntity<Void> saveFileToMongo() throws IOException {
        indexingService.parseJson();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/basicToElastic")
    public ResponseEntity<Void> basicToEs() {
        indexingService.indexBasicToEs();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/articleToElastic")
    public ResponseEntity<Void> articleToEs() {
        indexingService.indexArticleToEs();
        return ResponseEntity.ok().build();
    }
}
