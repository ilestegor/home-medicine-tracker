package org.ilestegor.home_medicine_tracker.application.service.regimen;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.GetRegimenQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetRegimenService implements GetRegimenQuery {

    private final RegimenPersistencePort regimens;
    private final CurrentUserPort currentUser;

    @Override
    public Regimen byId(Long id) {
        return regimens.requireById(id, currentUser.requireId());
    }
}
