package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.domain.model.User;

import java.time.Instant;

public record UserResponse(Long id, String email, String displayName, Instant createdAt) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getCreatedAt());
    }
}
