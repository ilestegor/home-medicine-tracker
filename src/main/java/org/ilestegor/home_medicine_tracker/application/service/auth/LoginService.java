package org.ilestegor.home_medicine_tracker.application.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.event.BusinessMetricEvents;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.LoginUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.command.LoginCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.model.Authenticated;
import org.ilestegor.home_medicine_tracker.application.port.out.AccessTokenPort;
import org.ilestegor.home_medicine_tracker.application.port.out.PasswordHasherPort;
import org.ilestegor.home_medicine_tracker.application.port.out.UserPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.AuthenticationFailedException;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class LoginService implements LoginUseCase {

    private static final String REJECTED = "Неверный email или пароль";

    private final UserPersistencePort users;
    private final PasswordHasherPort passwords;
    private final AccessTokenPort tokens;
    private final ApplicationEventPublisher publisher;

    @Override
    public Authenticated login(LoginCommand command) {
        User user = users.findByEmail(command.email()).orElse(null);
        if (user == null) {
            logFailure("unknown_email", null);
            publisher.publishEvent(new BusinessMetricEvents.LoginFailed(BusinessMetricEvents.LoginFailureReason.UNKNOWN_EMAIL));
            throw new AuthenticationFailedException(REJECTED);
        }
        if (!passwords.matches(command.password(), user.getPasswordHash())) {
            logFailure("bad_password", user.getId());
            publisher.publishEvent(new BusinessMetricEvents.LoginFailed(BusinessMetricEvents.LoginFailureReason.BAD_PASSWORD));
            throw new AuthenticationFailedException(REJECTED);
        }

        AccessTokenPort.IssuedToken issued = tokens.issue(user);

        log.atInfo()
                .setMessage("Вход выполнен")
                .addKeyValue("event", "auth.login.succeeded")
                .addKeyValue("userId", user.getId())
                .addKeyValue("tokenExpiresAt", issued.expiresAt())
                .log();

        publisher.publishEvent(new BusinessMetricEvents.LoginSucceeded());
        return new Authenticated(user, issued.token(), issued.expiresAt());
    }

    private void logFailure(String reason, Long userId) {
        log.atWarn()
                .setMessage("Неудачный вход")
                .addKeyValue("event", "auth.login.failed")
                .addKeyValue("reason", reason)
                .addKeyValue("userId", userId)
                .log();
    }
}
