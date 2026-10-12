package org.ilestegor.home_medicine_tracker.adapter.in.web;

import org.ilestegor.home_medicine_tracker.application.common.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {

    public static <E, D> PageResponse<D> of(PageResult<E> result, Function<E, D> mapper) {
        return new PageResponse<>(
                result.content().stream().map(mapper).toList(),
                result.page(), result.size(), result.totalElements(),
                result.totalPages(), result.hasNext());
    }
}
