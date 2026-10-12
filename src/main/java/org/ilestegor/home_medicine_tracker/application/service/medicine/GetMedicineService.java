package org.ilestegor.home_medicine_tracker.application.service.medicine;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.GetMedicineQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetMedicineService implements GetMedicineQuery {

    private final MedicinePersistencePort medicines;
    private final CurrentUserPort currentUser;

    @Override
    public Medicine byId(Long id) {
        return medicines.requireById(id, currentUser.requireId());
    }
}
