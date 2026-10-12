package org.ilestegor.home_medicine_tracker.application.service.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.GetTodayPlanQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model.TodayDose;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetTodayPlanService implements GetTodayPlanQuery {

    private final RegimenPersistencePort regimens;
    private final IntakePersistencePort intakes;
    private final StockItemPersistencePort stockItems;
    private final ClockPort clock;
    private final CurrentUserPort currentUser;

    @Override
    public List<TodayDose> today() {
        Long userId = currentUser.requireId();
        LocalDate today = clock.today();
        List<Regimen> effective = regimens.findEffectiveOn(today, userId);
        if (effective.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> taken = intakes.countByRegimenBetween(
                effective.stream().map(Regimen::getId).toList(),
                startOfDay(today), startOfDay(today.plusDays(1)), userId);

        Map<Long, BigDecimal> available = stockItems.sumAvailableByMedicine(
                effective.stream().map(r -> r.getMedicine().getId()).distinct().toList(),
                today, userId);

        return effective.stream().map(r -> toDose(r, today, taken, available)).toList();
    }

    private TodayDose toDose(Regimen regimen, LocalDate today,
                             Map<Long, Long> takenByRegimen,
                             Map<Long, BigDecimal> availableByMedicine) {
        long taken = takenByRegimen.getOrDefault(regimen.getId(), 0L);
        long remaining = Math.max(0, regimen.getTimesPerDay() - taken);

        BigDecimal available = availableByMedicine
                .getOrDefault(regimen.getMedicine().getId(), BigDecimal.ZERO);
        BigDecimal dailyDose = regimen.dailyDose();
        BigDecimal neededToday = regimen.getDoseAmount().multiply(BigDecimal.valueOf(remaining));

        long daysLeft = available.divide(dailyDose, 0, RoundingMode.DOWN).longValue();
        LocalDate runsOutOn = today.plusDays(daysLeft);

        Boolean lastsUntilEnd = regimen.getEndsOn() == null
                ? null
                : !runsOutOn.isBefore(regimen.getEndsOn());

        return new TodayDose(
                regimen.getId(), regimen.getMedicine(), regimen.getDoseAmount(),
                regimen.getTimesPerDay(), taken, remaining, dailyDose, available,
                available.compareTo(neededToday) >= 0, daysLeft, runsOutOn,
                regimen.getEndsOn(), lastsUntilEnd, regimen.getInstructions());
    }

    private Instant startOfDay(LocalDate day) {
        return day.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
    }
}
