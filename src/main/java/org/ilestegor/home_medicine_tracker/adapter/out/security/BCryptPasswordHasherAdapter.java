package org.ilestegor.home_medicine_tracker.adapter.out.security;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final PasswordEncoder encoder;

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hash) {
        return encoder.matches(rawPassword, hash);
    }
}
