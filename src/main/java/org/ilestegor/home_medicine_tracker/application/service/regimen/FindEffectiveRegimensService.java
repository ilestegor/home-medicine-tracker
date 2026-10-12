package org.ilestegor.home_medicine_tracker.application.service.regimen;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.FindEffectiveRegimensQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindEffectiveRegimensService implements FindEffectiveRegimensQuery {

    private final RegimenPersistencePort regimens;
    private final ClockPort clock;
    private final CurrentUserPort currentUser;

    @Override
    public List<Regimen> today() {
        return regimens.findEffectiveOn(clock.today(), currentUser.requireId());
    }
}
