package org.ilestegor.home_medicine_tracker.application.port.in.intake.command;

import java.time.Instant;

public record IntakeFilter(Long stockItemId, Long regimenId, Instant from, Instant to) {
}
