package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.command.CreateMedicineCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.command.UpdateMedicineCommand;
import org.ilestegor.home_medicine_tracker.domain.model.MedicineForm;

public record MedicineRequest(

        @NotBlank(message = "название обязательно")
        @Size(max = 200, message = "не длиннее 200 символов")
        String name,

        @Size(max = 200, message = "не длиннее 200 символов")
        String inn,

        MedicineForm form,

        @Size(max = 50, message = "не длиннее 50 символов")
        String dosage,

        @Size(max = 200, message = "не длиннее 200 символов")
        String manufacturer,

        String notes,

        @NotBlank(message = "единица измерения обязательна")
        @Size(max = 20, message = "не длиннее 20 символов")
        String unit
) {

    public CreateMedicineCommand toCreateCommand() {
        return new CreateMedicineCommand(name, inn, form, dosage, manufacturer, notes, unit);
    }

    public UpdateMedicineCommand toUpdateCommand(Long id) {
        return new UpdateMedicineCommand(id, name, inn, form, dosage, manufacturer, notes, unit);
    }
}
