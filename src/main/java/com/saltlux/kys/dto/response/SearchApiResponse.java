package com.saltlux.kys.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.saltlux.kys.domain.ArticleES;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchApiResponse {

    private long totalHits;
    private List<ArticleES> documents;

    private Map<String, Map<String, Long>> aggregations;
}