package com.saltlux.kys.dto.request;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record SearchFilterRequest(String category, String providerCode, String person,
                                  String organization, Integer minScore, Integer maxScore,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

}