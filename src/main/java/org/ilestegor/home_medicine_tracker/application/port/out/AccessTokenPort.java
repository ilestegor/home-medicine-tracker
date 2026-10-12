package org.ilestegor.home_medicine_tracker.application.port.out;

import org.ilestegor.home_medicine_tracker.domain.model.User;

import java.time.Instant;

public interface AccessTokenPort {

    IssuedToken issue(User user);

    record IssuedToken(String token, Instant expiresAt) {
    }
}
