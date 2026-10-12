package org.ilestegor.home_medicine_tracker.application.service.medicine;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.CreateMedicineUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.command.CreateMedicineCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class CreateMedicineService implements CreateMedicineUseCase {

    private final MedicinePersistencePort medicines;
    private final MedicineAssembler assembler;
    private final CurrentUserPort currentUser;

    @Override
    public Medicine create(CreateMedicineCommand command) {
        return medicines.save(assembler.fromCommand(command, currentUser.requireId()));
    }
}
