package org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model;

import org.ilestegor.home_medicine_tracker.domain.model.Medicine;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TodayDose(
        Long regimenId,
        Medicine medicine,
        BigDecimal doseAmount,
        short timesPerDay,
        long takenToday,
        long remainingToday,
        BigDecimal dailyDose,
        BigDecimal availableQuantity,
        boolean enoughForToday,
        long daysOfStockLeft,
        LocalDate stockRunsOutOn,
        LocalDate courseEndsOn,
        Boolean stockLastsUntilCourseEnd,
        String instructions
) {
}
