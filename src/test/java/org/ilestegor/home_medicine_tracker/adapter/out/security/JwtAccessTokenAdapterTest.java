package org.ilestegor.home_medicine_tracker.adapter.out.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.ilestegor.home_medicine_tracker.application.port.out.AccessTokenPort;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.config.SecurityConfig;
import org.ilestegor.home_medicine_tracker.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAccessTokenAdapterTest {

    private static final String SECRET = "test-secret-not-for-production-32b+";
    private static final SecretKey KEY =
            new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    private static final Duration TTL = Duration.ofHours(24);

    private final Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    private JwtAccessTokenAdapter adapter() {
        return new JwtAccessTokenAdapter(
                new NimbusJwtEncoder(new ImmutableSecret<>(KEY)), new FixedClock(now), TTL);
    }

    private Jwt decode(String token) {
        return NimbusJwtDecoder.withSecretKey(KEY).macAlgorithm(MacAlgorithm.HS256)
                .build().decode(token);
    }

    @Test
    @DisplayName("В токене лежит id пользователя и срок жизни из настроек")
    void issuedTokenCarriesUserIdAndExpiry() {
        AccessTokenPort.IssuedToken issued = adapter().issue(
                User.builder().id(42L).email("owner@example.test").build());

        Jwt decoded = decode(issued.token());

        assertThat(decoded.getSubject()).isEqualTo("42");
        assertThat(decoded.getClaimAsString("email")).isEqualTo("owner@example.test");
        assertThat(issued.expiresAt()).isEqualTo(now.plus(TTL));
        assertThat(decoded.getExpiresAt()).isEqualTo(now.plus(TTL));
    }

    @Test
    @DisplayName("Токен, подписанный другим секретом, не принимается")
    void tokenSignedWithAnotherSecretIsRejected() {
        SecretKey foreign = new SecretKeySpec(
                "another-secret-of-at-least-32-bytes".getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        String token = new JwtAccessTokenAdapter(
                new NimbusJwtEncoder(new ImmutableSecret<>(foreign)), new FixedClock(now), TTL)
                .issue(User.builder().id(1L).email("a@b.test").build())
                .token();

        assertThatThrownBy(() -> decode(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Слишком короткий секрет не даёт приложению стартовать")
    void shortSecretIsRefusedAtStartup() {
        assertThatThrownBy(() -> new SecurityConfig().jwtSecretKey("too-short"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32");
    }

    private record FixedClock(Instant instant) implements ClockPort {
        @Override
        public Instant now() {
            return instant;
        }

        @Override
        public LocalDate today() {
            return LocalDate.ofInstant(instant, java.time.ZoneOffset.UTC);
        }
    }
}
