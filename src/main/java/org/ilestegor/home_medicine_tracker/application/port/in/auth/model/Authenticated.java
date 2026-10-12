package org.ilestegor.home_medicine_tracker.application.port.in.auth.model;

import org.ilestegor.home_medicine_tracker.domain.model.User;

import java.time.Instant;

public record Authenticated(User user, String token, Instant expiresAt) {
}
