package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.domain.model.Regimen;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RegimenResponse(
        Long id,
        MedicineSummary medicine,
        BigDecimal doseAmount,
        Short timesPerDay,
        BigDecimal dailyDose,
        LocalDate startsOn,
        LocalDate endsOn,
        Long totalDays,
        boolean effectiveToday,
        String instructions,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static RegimenResponse from(Regimen r, LocalDate today) {
        return new RegimenResponse(
                r.getId(),
                MedicineSummary.from(r.getMedicine()),
                r.getDoseAmount(),
                r.getTimesPerDay(),
                r.dailyDose(),
                r.getStartsOn(),
                r.getEndsOn(),
                r.totalDays(),
                r.isEffectiveOn(today),
                r.getInstructions(),
                r.isActive(),
                r.getCreatedAt(),
                r.getUpdatedAt());
    }
}
