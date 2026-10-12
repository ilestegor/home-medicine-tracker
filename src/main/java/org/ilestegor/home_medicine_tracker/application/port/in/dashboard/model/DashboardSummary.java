package org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model;

import java.time.LocalDate;

public record DashboardSummary(
        LocalDate date,
        long medicinesCount,
        int activeStockItems,
        int expiredCount,
        int expiringSoonCount,
        int lowStockCount,
        int effectiveRegimens,
        long dosesPlannedToday,
        long dosesTakenToday,
        long dosesRemainingToday,
        long regimensShortOnStockToday
) {
}
