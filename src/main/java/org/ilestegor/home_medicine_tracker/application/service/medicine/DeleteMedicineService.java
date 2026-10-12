package org.ilestegor.home_medicine_tracker.application.service.medicine;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.DeleteMedicineUseCase;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class DeleteMedicineService implements DeleteMedicineUseCase {

    private final MedicinePersistencePort medicines;
    private final CurrentUserPort currentUser;

    @Override
    public void delete(Long id) {
        Long userId = currentUser.requireId();
        medicines.requireById(id, userId);
        medicines.deleteById(id, userId);
    }
}
