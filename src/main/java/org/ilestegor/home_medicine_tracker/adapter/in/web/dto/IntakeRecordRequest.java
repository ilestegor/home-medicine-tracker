package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.RecordIntakeCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.UpdateIntakeCommand;

import java.math.BigDecimal;
import java.time.Instant;

public record IntakeRecordRequest(

        @NotNull(message = "упаковка обязательна")
        Long stockItemId,

        Long regimenId,

        @NotNull(message = "количество обязательно")
        @DecimalMin(value = "0.01", message = "количество должно быть больше нуля")
        @Digits(integer = 8, fraction = 2, message = "не более 8 цифр и 2 знаков после запятой")
        BigDecimal amount,

        Instant takenAt,

        String note
) {

    public RecordIntakeCommand toRecordCommand() {
        return new RecordIntakeCommand(stockItemId, regimenId, amount, takenAt, note);
    }

    public UpdateIntakeCommand toUpdateCommand(Long id) {
        return new UpdateIntakeCommand(id, stockItemId, regimenId, amount, takenAt, note);
    }
}
