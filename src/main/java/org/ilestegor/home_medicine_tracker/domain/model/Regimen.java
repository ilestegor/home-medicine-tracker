package org.ilestegor.home_medicine_tracker.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
public class Regimen {

    private final Long id;

    private final Medicine medicine;

    @Setter
    private BigDecimal doseAmount;

    @Setter
    private Short timesPerDay;

    @Setter
    private LocalDate startsOn;

    @Setter
    private LocalDate endsOn;

    @Setter
    private String instructions;

    @Builder.Default
    @Setter
    private boolean active = true;

    private final Instant createdAt;
    private final Instant updatedAt;

    public String doseUnit() {
        return medicine.getUnit();
    }

    public boolean isEffectiveOn(LocalDate date) {
        if (!active || startsOn.isAfter(date)) {
            return false;
        }
        return endsOn == null || !endsOn.isBefore(date);
    }

    public BigDecimal dailyDose() {
        return doseAmount.multiply(BigDecimal.valueOf(timesPerDay));
    }

    public Long totalDays() {
        return endsOn == null ? null : startsOn.until(endsOn).getDays() + 1L;
    }
}
