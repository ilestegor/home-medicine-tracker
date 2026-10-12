package org.ilestegor.home_medicine_tracker.application.common;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(List<T> content, int page, int size, long totalElements) {

    public int totalPages() {
        return size == 0 ? 0 : (int) Math.ceilDiv(totalElements, (long) size);
    }

    public boolean hasNext() {
        return (long) (page + 1) * size < totalElements;
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(content.stream().map(mapper).toList(),
                page, size, totalElements);
    }
}
