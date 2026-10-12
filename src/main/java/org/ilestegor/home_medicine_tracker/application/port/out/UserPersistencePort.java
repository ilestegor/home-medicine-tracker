package org.ilestegor.home_medicine_tracker.application.port.out;

import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.User;

import java.util.Optional;

public interface UserPersistencePort {

    Optional<User> findById(Long id);

    default User requireById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Пользователь", id));
    }

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}
