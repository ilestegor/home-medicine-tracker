package org.ilestegor.home_medicine_tracker.application.service.regimen;

import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.CreateRegimenCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.UpdateRegimenCommand;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
class RegimenAssembler {

    Regimen fromCommand(CreateRegimenCommand c, Medicine medicine) {
        requireSanePeriod(c.startsOn(), c.endsOn());
        return Regimen.builder()
                .medicine(medicine)
                .doseAmount(c.doseAmount())
                .timesPerDay(c.timesPerDay())
                .startsOn(c.startsOn())
                .endsOn(c.endsOn())
                .instructions(c.instructions())
                .active(c.active())
                .build();
    }

    void apply(UpdateRegimenCommand c, Regimen target) {
        requireSanePeriod(c.startsOn(), c.endsOn());
        target.setDoseAmount(c.doseAmount());
        target.setTimesPerDay(c.timesPerDay());
        target.setStartsOn(c.startsOn());
        target.setEndsOn(c.endsOn());
        target.setInstructions(c.instructions());
        target.setActive(c.active());
    }

    private void requireSanePeriod(LocalDate startsOn, LocalDate endsOn) {
        if (endsOn != null && endsOn.isBefore(startsOn)) {
            throw new BusinessRuleException("Дата окончания раньше даты начала");
        }
    }
}
