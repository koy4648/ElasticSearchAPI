package com.saltlux.kys.dto.response;

import com.saltlux.kys.domain.ArticleES;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class SearchApiResponse {

    private long totalHits;
    private List<ArticleES> documents;

}