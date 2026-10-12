package org.ilestegor.home_medicine_tracker.adapter.out.security;

import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.domain.exception.AuthenticationFailedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class SecurityContextUserAdapter implements CurrentUserPort {

    @Override
    public Long requireId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwt)) {
            throw new AuthenticationFailedException("Требуется вход в систему");
        }
        try {
            return Long.valueOf(Objects.requireNonNull(jwt.getToken().getSubject()));
        } catch (NumberFormatException e) {
            throw new AuthenticationFailedException("Токен повреждён");
        }
    }
}
