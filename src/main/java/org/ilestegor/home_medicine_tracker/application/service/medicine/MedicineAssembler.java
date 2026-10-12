package org.ilestegor.home_medicine_tracker.application.service.medicine;

import org.ilestegor.home_medicine_tracker.application.port.in.medicine.command.CreateMedicineCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.command.UpdateMedicineCommand;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.springframework.stereotype.Component;

@Component
class MedicineAssembler {

    Medicine fromCommand(CreateMedicineCommand c, Long userId) {
        return Medicine.builder()
                .userId(userId)
                .name(c.name())
                .inn(c.inn())
                .form(c.form())
                .dosage(c.dosage())
                .manufacturer(c.manufacturer())
                .notes(c.notes())
                .unit(c.unit())
                .build();
    }

    void apply(UpdateMedicineCommand c, Medicine target) {
        target.setName(c.name());
        target.setInn(c.inn());
        target.setForm(c.form());
        target.setDosage(c.dosage());
        target.setManufacturer(c.manufacturer());
        target.setNotes(c.notes());
        target.setUnit(c.unit());
    }
}
