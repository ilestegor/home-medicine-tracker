package org.ilestegor.home_medicine_tracker.application.common;

public final class Text {

    private Text() {
    }

    public static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.strip();
    }

    public static String strip(String value) {
        return value == null ? null : value.strip();
    }
}
