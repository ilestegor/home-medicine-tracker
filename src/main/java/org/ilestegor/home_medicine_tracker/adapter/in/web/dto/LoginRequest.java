package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.command.LoginCommand;

public record LoginRequest(

        @NotBlank(message = "email обязателен")
        String email,

        @NotBlank(message = "пароль обязателен")
        String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
