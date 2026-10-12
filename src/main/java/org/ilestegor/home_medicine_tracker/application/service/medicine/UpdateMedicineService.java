package org.ilestegor.home_medicine_tracker.application.service.medicine;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.UpdateMedicineUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.command.UpdateMedicineCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class UpdateMedicineService implements UpdateMedicineUseCase {

    private final MedicinePersistencePort medicines;
    private final MedicineAssembler assembler;
    private final CurrentUserPort currentUser;

    @Override
    public Medicine update(UpdateMedicineCommand command) {
        Medicine medicine = medicines.requireById(command.id(), currentUser.requireId());
        assembler.apply(command, medicine);
        return medicines.save(medicine);
    }
}
