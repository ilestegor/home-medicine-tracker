package org.ilestegor.home_medicine_tracker.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.UserJpaEntity;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper.UserPersistenceMapper;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.UserJpaRepository;
import org.ilestegor.home_medicine_tracker.application.port.out.UserPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class UserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository repository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = user.getId() == null
                ? new UserJpaEntity()
                : repository.findById(user.getId())
                        .orElseThrow(() -> new NotFoundException("Пользователь", user.getId()));

        mapper.applyToJpa(user, entity);
        return mapper.toDomain(repository.save(entity));
    }
}
