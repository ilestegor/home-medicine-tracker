package org.ilestegor.home_medicine_tracker.application.service.intake;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.GetIntakeQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetIntakeService implements GetIntakeQuery {

    private final IntakePersistencePort intakes;
    private final CurrentUserPort currentUser;

    @Override
    public IntakeRecord byId(Long id) {
        return intakes.requireById(id, currentUser.requireId());
    }
}
