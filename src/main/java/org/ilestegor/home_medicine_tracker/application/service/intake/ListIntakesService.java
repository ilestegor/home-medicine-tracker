package org.ilestegor.home_medicine_tracker.application.service.intake;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.common.PageRequest;
import org.ilestegor.home_medicine_tracker.application.common.PageResult;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.ListIntakesQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.IntakeFilter;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ListIntakesService implements ListIntakesQuery {

    private final IntakePersistencePort intakes;
    private final CurrentUserPort currentUser;

    @Override
    public PageResult<IntakeRecord> list(IntakeFilter filter, PageRequest page) {
        return intakes.search(filter, page, currentUser.requireId());
    }
}
