package org.ilestegor.home_medicine_tracker.application.service.regimen;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.CreateRegimenUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.CreateRegimenCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class CreateRegimenService implements CreateRegimenUseCase {

    private final RegimenPersistencePort regimens;
    private final MedicinePersistencePort medicines;
    private final RegimenAssembler assembler;
    private final CurrentUserPort currentUser;

    @Override
    public Regimen create(CreateRegimenCommand command) {
        return regimens.save(assembler.fromCommand(
                command, medicines.requireById(command.medicineId(), currentUser.requireId())));
    }
}
