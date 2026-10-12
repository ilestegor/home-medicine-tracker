package org.ilestegor.home_medicine_tracker.application.port.in.medicine;

import org.ilestegor.home_medicine_tracker.application.port.in.medicine.command.CreateMedicineCommand;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;

public interface CreateMedicineUseCase {

    Medicine create(CreateMedicineCommand command);
}
