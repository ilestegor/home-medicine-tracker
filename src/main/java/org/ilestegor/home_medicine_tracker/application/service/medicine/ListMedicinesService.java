package org.ilestegor.home_medicine_tracker.application.service.medicine;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.ListMedicinesQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ListMedicinesService implements ListMedicinesQuery {

    private final MedicinePersistencePort medicines;
    private final CurrentUserPort currentUser;

    @Override
    public List<Medicine> search(String query) {
        return medicines.search(query, currentUser.requireId());
    }
}
