package org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper;

import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.UserJpaEntity;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity e) {
        return User.builder()
                .id(e.getId())
                .email(e.getEmail())
                .passwordHash(e.getPasswordHash())
                .displayName(e.getDisplayName())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public void applyToJpa(User u, UserJpaEntity target) {
        target.setEmail(u.getEmail());
        target.setPasswordHash(u.getPasswordHash());
        target.setDisplayName(u.getDisplayName());
    }
}
