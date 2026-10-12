package org.ilestegor.home_medicine_tracker.application.service.regimen;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.ListRegimensQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.RegimenFilter;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ListRegimensService implements ListRegimensQuery {

    private final RegimenPersistencePort regimens;
    private final CurrentUserPort currentUser;

    @Override
    public List<Regimen> list(RegimenFilter filter) {
        return regimens.search(filter, currentUser.requireId());
    }
}
