package org.ilestegor.home_medicine_tracker.adapter.out.security;

import org.ilestegor.home_medicine_tracker.application.port.out.AccessTokenPort;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
class JwtAccessTokenAdapter implements AccessTokenPort {

    private static final String ISSUER = "home-medicine-tracker";

    private static final JwsHeader HEADER = JwsHeader.with(MacAlgorithm.HS256).build();

    private final JwtEncoder encoder;
    private final ClockPort clock;
    private final Duration ttl;

    JwtAccessTokenAdapter(JwtEncoder encoder, ClockPort clock,
                          @Value("${app.jwt.ttl}") Duration ttl) {
        this.encoder = encoder;
        this.clock = clock;
        this.ttl = ttl;
    }

    @Override
    public IssuedToken issue(User user) {
        Instant now = clock.now();
        Instant expiresAt = now.plus(ttl);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .build();

        return new IssuedToken(
                encoder.encode(JwtEncoderParameters.from(HEADER, claims)).getTokenValue(),
                expiresAt);
    }
}
