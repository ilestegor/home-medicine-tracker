package org.ilestegor.home_medicine_tracker.application.service.intake;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
class IntakeRules {

    private final RegimenPersistencePort regimens;
    private final ClockPort clock;

    Regimen resolveRegimen(Long regimenId, StockItem stock, Long userId) {
        if (regimenId == null) {
            return null;
        }
        Regimen regimen = regimens.requireById(regimenId, userId);

        if (!regimen.getMedicine().getId().equals(stock.getMedicine().getId())) {
            throw new BusinessRuleException(
                    "Схема приёма назначена для «%s», а упаковка — «%s»".formatted(
                            regimen.getMedicine().getName(), stock.getMedicine().getName()));
        }
        return regimen;
    }

    void requireNotExpired(StockItem stock) {
        if (stock.isExpiredOn(clock.today())) {
            throw new BusinessRuleException(
                    "Упаковка просрочена (до %s) — принимать из неё нельзя"
                            .formatted(stock.getExpiresAt()));
        }
    }

    Instant resolveTakenAt(Instant requested, Instant fallback) {
        Instant takenAt = requested != null ? requested : fallback;
        if (takenAt.isAfter(clock.now())) {
            throw new BusinessRuleException("Время приёма не может быть в будущем");
        }
        return takenAt;
    }

    Instant now() {
        return clock.now();
    }
}
