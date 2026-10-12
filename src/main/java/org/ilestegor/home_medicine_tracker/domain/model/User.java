package org.ilestegor.home_medicine_tracker.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class User {

    private final Long id;

    private final String email;

    @Setter
    private String passwordHash;

    @Setter
    private String displayName;

    private final Instant createdAt;
    private final Instant updatedAt;
}
