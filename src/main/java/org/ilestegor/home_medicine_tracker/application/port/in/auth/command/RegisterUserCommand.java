package org.ilestegor.home_medicine_tracker.application.port.in.auth.command;

import org.ilestegor.home_medicine_tracker.application.common.Text;

import java.util.Locale;

public record RegisterUserCommand(String email, String password, String displayName) {

    public RegisterUserCommand {
        email = normalizeEmail(email);
        displayName = Text.blankToNull(displayName);
    }

    public static String normalizeEmail(String email) {
        String stripped = Text.blankToNull(email);
        return stripped == null ? null : stripped.toLowerCase(Locale.ROOT);
    }
}
