package org.ilestegor.home_medicine_tracker.domain.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String what, Long id) {
        super("%s с id=%d не найден".formatted(what, id));
    }
}
