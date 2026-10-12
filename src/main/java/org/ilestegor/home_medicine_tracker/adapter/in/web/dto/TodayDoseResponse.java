package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model.TodayDose;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TodayDoseResponse(
        Long regimenId,
        MedicineSummary medicine,
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

    public static TodayDoseResponse from(TodayDose d) {
        return new TodayDoseResponse(
                d.regimenId(), MedicineSummary.from(d.medicine()), d.doseAmount(),
                d.timesPerDay(), d.takenToday(), d.remainingToday(), d.dailyDose(),
                d.availableQuantity(), d.enoughForToday(), d.daysOfStockLeft(),
                d.stockRunsOutOn(), d.courseEndsOn(), d.stockLastsUntilCourseEnd(),
                d.instructions());
    }
}
