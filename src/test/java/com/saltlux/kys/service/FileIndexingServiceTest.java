package com.saltlux.kys.service;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileIndexingServiceTest {

    @Autowired
    private IndexingService indexingService;

    @Test
    void testJsonImport() throws IOException {
        indexingService.parseJson();
    }
    @Test
    void testMongoToEs(){
        indexingService.indexToEs();
    }
}
