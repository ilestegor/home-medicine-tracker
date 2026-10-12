package org.ilestegor.home_medicine_tracker.adapter.out.time;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SystemClockAdapter implements ClockPort {

    private final Clock clock;

    @Override
    public Instant now() {
        return Instant.now(clock);
    }

    @Override
    public LocalDate today() {
        return LocalDate.now(clock);
    }
}
