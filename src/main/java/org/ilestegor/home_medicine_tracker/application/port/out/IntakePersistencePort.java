package org.ilestegor.home_medicine_tracker.application.port.out;

import org.ilestegor.home_medicine_tracker.application.common.PageRequest;
import org.ilestegor.home_medicine_tracker.application.common.PageResult;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.IntakeFilter;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface IntakePersistencePort {

    Optional<IntakeRecord> findById(Long id, Long userId);

    default IntakeRecord requireById(Long id, Long userId) {
        return findById(id, userId).orElseThrow(() -> new NotFoundException("Запись приёма", id));
    }

    PageResult<IntakeRecord> search(IntakeFilter filter, PageRequest page, Long userId);

    IntakeRecord save(IntakeRecord record);

    void deleteById(Long id, Long userId);

    Map<Long, Long> countByRegimenBetween(Collection<Long> regimenIds, Instant from, Instant to, Long userId);
}
