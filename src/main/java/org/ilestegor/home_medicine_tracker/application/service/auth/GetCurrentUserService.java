package org.ilestegor.home_medicine_tracker.application.service.auth;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.GetCurrentUserQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.UserPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetCurrentUserService implements GetCurrentUserQuery {

    private final UserPersistencePort users;
    private final CurrentUserPort currentUser;

    @Override
    public User current() {
        return users.requireById(currentUser.requireId());
    }
}
