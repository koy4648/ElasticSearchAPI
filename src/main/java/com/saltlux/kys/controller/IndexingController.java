package com.saltlux.kys.controller;

import com.saltlux.kys.service.IndexingService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/index")
public class IndexingController {

    private final IndexingService indexingService;

    @GetMapping("/fileToMongo")
    public ResponseEntity<String> saveFileToMongo() {
        try {
            indexingService.parseJson();
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/mongoToElastic")
    public ResponseEntity<String> saveMongoToElastic() {
        try {
            indexingService.indexToEs();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
