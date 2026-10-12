package org.ilestegor.home_medicine_tracker.application.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.LoginUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.command.LoginCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.model.Authenticated;
import org.ilestegor.home_medicine_tracker.application.port.out.AccessTokenPort;
import org.ilestegor.home_medicine_tracker.application.port.out.PasswordHasherPort;
import org.ilestegor.home_medicine_tracker.application.port.out.UserPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.AuthenticationFailedException;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class LoginService implements LoginUseCase {

    private final UserPersistencePort users;
    private final PasswordHasherPort passwords;
    private final AccessTokenPort tokens;

    @Override
    public Authenticated login(LoginCommand command) {
        User user = users.findByEmail(command.email())
                .filter(u -> passwords.matches(command.password(), u.getPasswordHash()))
                .orElseThrow(() -> {
                    log.warn("Неудачный вход: {}", command.email());
                    return new AuthenticationFailedException("Неверный email или пароль");
                });

        AccessTokenPort.IssuedToken issued = tokens.issue(user);
        log.info("Вход выполнен: userId={}", user.getId());
        return new Authenticated(user, issued.token(), issued.expiresAt());
    }
}
