package org.ilestegor.home_medicine_tracker.application.service.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.GetDashboardSummaryQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.GetTodayPlanQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model.DashboardSummary;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model.TodayDose;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetDashboardSummaryService implements GetDashboardSummaryQuery {

    private static final int LOW_STOCK_THRESHOLD_DAYS = 3;
    private static final int EXPIRING_SOON_DAYS = 30;

    private final GetTodayPlanQuery todayPlan;
    private final MedicinePersistencePort medicines;
    private final StockItemPersistencePort stockItems;
    private final ClockPort clock;
    private final CurrentUserPort currentUser;

    @Override
    public DashboardSummary summary() {
        Long userId = currentUser.requireId();
        LocalDate today = clock.today();
        List<TodayDose> doses = todayPlan.today();

        return new DashboardSummary(
                today,
                medicines.count(userId),
                stockItems.countActive(userId),
                stockItems.findActiveExpiredBefore(today, userId).size(),
                stockItems.findActiveExpiringUntil(
                        today.plusDays(EXPIRING_SOON_DAYS), userId).size(),
                stockItems.findActiveBelow(lowStockThreshold(doses), userId).size(),
                doses.size(),
                doses.stream().mapToLong(TodayDose::timesPerDay).sum(),
                doses.stream().mapToLong(TodayDose::takenToday).sum(),
                doses.stream().mapToLong(TodayDose::remainingToday).sum(),
                doses.stream().filter(d -> !d.enoughForToday()).count());
    }

    private BigDecimal lowStockThreshold(List<TodayDose> doses) {
        return doses.stream()
                .map(TodayDose::dailyDose)
                .max(BigDecimal::compareTo)
                .map(max -> max.multiply(BigDecimal.valueOf(LOW_STOCK_THRESHOLD_DAYS)))
                .orElse(BigDecimal.valueOf(5));
    }
}
