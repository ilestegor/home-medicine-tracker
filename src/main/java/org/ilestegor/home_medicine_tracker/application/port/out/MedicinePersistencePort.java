package org.ilestegor.home_medicine_tracker.application.port.out;

import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;

import java.util.List;
import java.util.Optional;

public interface MedicinePersistencePort {

    Optional<Medicine> findById(Long id, Long userId);

    default Medicine requireById(Long id, Long userId) {
        return findById(id, userId).orElseThrow(() -> new NotFoundException("Препарат", id));
    }

    List<Medicine> search(String query, Long userId);

    Medicine save(Medicine medicine);

    void deleteById(Long id, Long userId);

    long count(Long userId);
}
