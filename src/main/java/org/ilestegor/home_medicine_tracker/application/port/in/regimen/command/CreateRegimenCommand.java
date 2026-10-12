package org.ilestegor.home_medicine_tracker.application.port.in.regimen.command;

import org.ilestegor.home_medicine_tracker.application.common.Text;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRegimenCommand(
        Long medicineId,
        BigDecimal doseAmount,
        Short timesPerDay,
        LocalDate startsOn,
        LocalDate endsOn,
        String instructions,
        Boolean active
) {
    public CreateRegimenCommand {
        instructions = Text.blankToNull(instructions);
        active = active == null || active;
    }
}
