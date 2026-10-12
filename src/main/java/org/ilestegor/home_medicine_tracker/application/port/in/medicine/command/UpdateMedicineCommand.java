package org.ilestegor.home_medicine_tracker.application.port.in.medicine.command;

import org.ilestegor.home_medicine_tracker.application.common.Text;
import org.ilestegor.home_medicine_tracker.domain.model.MedicineForm;

public record UpdateMedicineCommand(
        Long id,
        String name,
        String inn,
        MedicineForm form,
        String dosage,
        String manufacturer,
        String notes,
        String unit
) {
    public UpdateMedicineCommand {
        name = Text.strip(name);
        inn = Text.blankToNull(inn);
        dosage = Text.blankToNull(dosage);
        manufacturer = Text.blankToNull(manufacturer);
        notes = Text.blankToNull(notes);
        unit = Text.strip(unit);
    }
}
