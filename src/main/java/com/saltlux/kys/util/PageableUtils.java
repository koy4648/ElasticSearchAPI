package com.saltlux.kys.util;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    private static final List<String> SAFE_SORT_FIELDS = List.of("publishedAt", "title");

    public static Pageable sanitize(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, 10);
        }

        Sort safeSort = pageable.getSort().stream()
            .filter(order -> SAFE_SORT_FIELDS.contains(order.getProperty()))
            .collect(Collectors.collectingAndThen(Collectors.toList(), Sort::by));

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
    }

    public static Pageable stripSort(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }
}