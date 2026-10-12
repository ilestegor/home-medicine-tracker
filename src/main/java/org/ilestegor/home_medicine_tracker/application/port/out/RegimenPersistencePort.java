package org.ilestegor.home_medicine_tracker.application.port.out;

import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.RegimenFilter;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegimenPersistencePort {

    Optional<Regimen> findById(Long id, Long userId);

    default Regimen requireById(Long id, Long userId) {
        return findById(id, userId).orElseThrow(() -> new NotFoundException("Схема приёма", id));
    }

    List<Regimen> search(RegimenFilter filter, Long userId);

    Regimen save(Regimen regimen);

    void deleteById(Long id, Long userId);

    List<Regimen> findEffectiveOn(LocalDate date, Long userId);
}
