package org.ilestegor.home_medicine_tracker.application.common;

public record PageRequest(int page, int size) {

    public static final int DEFAULT_SIZE = 50;
    public static final int MAX_SIZE = 200;

    public PageRequest {
        page = Math.max(0, page);
        size = Math.clamp(size, 1, MAX_SIZE);
    }

    public static PageRequest of(Integer page, Integer size) {
        return new PageRequest(
                page == null ? 0 : page,
                size == null ? DEFAULT_SIZE : size);
    }

    public int offset() {
        return page * size;
    }
}
