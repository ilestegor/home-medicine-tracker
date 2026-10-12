package org.ilestegor.home_medicine_tracker.application.port.out;

import java.time.Instant;
import java.time.LocalDate;

public interface ClockPort {

    Instant now();

    LocalDate today();
}
