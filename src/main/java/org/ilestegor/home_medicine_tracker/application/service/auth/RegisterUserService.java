package org.ilestegor.home_medicine_tracker.application.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.event.BusinessMetricEvents;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.RegisterUserUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.command.RegisterUserCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.model.Authenticated;
import org.ilestegor.home_medicine_tracker.application.port.out.AccessTokenPort;
import org.ilestegor.home_medicine_tracker.application.port.out.PasswordHasherPort;
import org.ilestegor.home_medicine_tracker.application.port.out.UserPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class RegisterUserService implements RegisterUserUseCase {

    private final UserPersistencePort users;
    private final PasswordHasherPort passwords;
    private final AccessTokenPort tokens;
    private final ApplicationEventPublisher publisher;

    @Override
    public Authenticated register(RegisterUserCommand command) {
        if (users.existsByEmail(command.email())) {
            throw new BusinessRuleException("Пользователь с таким email уже зарегистрирован");
        }

        User saved = users.save(User.builder()
                .email(command.email())
                .passwordHash(passwords.hash(command.password()))
                .displayName(command.displayName())
                .build());

        log.atInfo()
                .setMessage("Регистрация выполнена")
                .addKeyValue("event", "auth.register.succeeded")
                .addKeyValue("userId", saved.getId())
                .log();

        AccessTokenPort.IssuedToken issued = tokens.issue(saved);
        publisher.publishEvent(new BusinessMetricEvents.UserRegistered());
        return new Authenticated(saved, issued.token(), issued.expiresAt());
    }
}
