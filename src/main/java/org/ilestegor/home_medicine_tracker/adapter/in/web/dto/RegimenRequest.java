package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.CreateRegimenCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.UpdateRegimenCommand;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegimenRequest(

        @NotNull(message = "препарат обязателен")
        Long medicineId,

        @NotNull(message = "доза обязательна")
        @DecimalMin(value = "0.01", message = "доза должна быть больше нуля")
        @Digits(integer = 8, fraction = 2, message = "не более 8 цифр и 2 знаков после запятой")
        BigDecimal doseAmount,

        @NotNull(message = "частота обязательна")
        @Min(value = 1, message = "минимум один раз в день")
        @Max(value = 24, message = "не чаще 24 раз в день")
        Short timesPerDay,

        @NotNull(message = "дата начала обязательна")
        LocalDate startsOn,

        LocalDate endsOn,

        String instructions,

        Boolean active
) {

    public CreateRegimenCommand toCreateCommand() {
        return new CreateRegimenCommand(medicineId, doseAmount, timesPerDay,
                startsOn, endsOn, instructions, active);
    }

    public UpdateRegimenCommand toUpdateCommand(Long id) {
        return new UpdateRegimenCommand(id, medicineId, doseAmount, timesPerDay,
                startsOn, endsOn, instructions, active);
    }
}
