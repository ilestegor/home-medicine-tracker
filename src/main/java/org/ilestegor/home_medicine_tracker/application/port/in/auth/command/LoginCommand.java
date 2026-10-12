package org.ilestegor.home_medicine_tracker.application.port.in.auth.command;

public record LoginCommand(String email, String password) {

    public LoginCommand {
        email = RegisterUserCommand.normalizeEmail(email);
    }
}
