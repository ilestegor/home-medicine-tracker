package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.application.port.in.auth.model.Authenticated;

import java.time.Instant;

public record AuthResponse(String token, Instant expiresAt, UserResponse user) {

    public static AuthResponse from(Authenticated a) {
        return new AuthResponse(a.token(), a.expiresAt(), UserResponse.from(a.user()));
    }
}
