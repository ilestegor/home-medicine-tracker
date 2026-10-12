package org.ilestegor.home_medicine_tracker.application.port.in.intake.command;

import org.ilestegor.home_medicine_tracker.application.common.Text;

import java.math.BigDecimal;
import java.time.Instant;

public record RecordIntakeCommand(
        Long stockItemId,
        Long regimenId,
        BigDecimal amount,
        Instant takenAt,
        String note
) {
    public RecordIntakeCommand {
        note = Text.blankToNull(note);
    }
}
