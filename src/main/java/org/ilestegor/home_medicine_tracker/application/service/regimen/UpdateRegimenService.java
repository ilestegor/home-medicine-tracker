package org.ilestegor.home_medicine_tracker.application.service.regimen;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.UpdateRegimenUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.UpdateRegimenCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class UpdateRegimenService implements UpdateRegimenUseCase {

    private final RegimenPersistencePort regimens;
    private final RegimenAssembler assembler;
    private final CurrentUserPort currentUser;

    @Override
    public Regimen update(UpdateRegimenCommand command) {
        Regimen regimen = regimens.requireById(command.id(), currentUser.requireId());

        if (!regimen.getMedicine().getId().equals(command.medicineId())) {
            throw new BusinessRuleException(
                    "Препарат у существующей схемы изменить нельзя — создай новую схему");
        }
        assembler.apply(command, regimen);
        return regimens.save(regimen);
    }
}
