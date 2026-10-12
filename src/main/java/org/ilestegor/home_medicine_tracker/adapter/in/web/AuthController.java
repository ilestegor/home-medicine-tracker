package org.ilestegor.home_medicine_tracker.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.AuthResponse;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.LoginRequest;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.RegisterRequest;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.UserResponse;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.GetCurrentUserQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.LoginUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.RegisterUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final LoginUseCase login;
    private final GetCurrentUserQuery currentUser;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return AuthResponse.from(registerUser.register(request.toCommand()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return AuthResponse.from(login.login(request.toCommand()));
    }

    @GetMapping("/me")
    public UserResponse me() {
        return UserResponse.from(currentUser.current());
    }
}
