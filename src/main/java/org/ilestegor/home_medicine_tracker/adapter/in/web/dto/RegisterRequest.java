package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.command.RegisterUserCommand;

public record RegisterRequest(

        @NotBlank(message = "email обязателен")
        @Email(message = "непохоже на email")
        @Size(max = 320, message = "не длиннее 320 символов")
        String email,

        @NotBlank(message = "пароль обязателен")
        @Size(min = 8, max = 72, message = "от 8 до 72 символов")
        String password,

        @Size(max = 100, message = "не длиннее 100 символов")
        String displayName
) {

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(email, password, displayName);
    }
}
