package org.ilestegor.home_medicine_tracker.application.service.regimen;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.DeleteRegimenUseCase;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class DeleteRegimenService implements DeleteRegimenUseCase {

    private final RegimenPersistencePort regimens;
    private final CurrentUserPort currentUser;

    @Override
    public void delete(Long id) {
        Long userId = currentUser.requireId();
        regimens.requireById(id, userId);
        regimens.deleteById(id, userId);
    }
}
