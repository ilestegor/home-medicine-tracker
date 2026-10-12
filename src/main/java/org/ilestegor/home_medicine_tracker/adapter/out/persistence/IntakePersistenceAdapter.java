package org.ilestegor.home_medicine_tracker.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.IntakeRecordJpaEntity;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper.IntakeRecordPersistenceMapper;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.projection.RegimenIntakeCount;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.IntakeRecordJpaRepository;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.RegimenJpaRepository;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.StockItemJpaRepository;
import org.ilestegor.home_medicine_tracker.application.common.PageRequest;
import org.ilestegor.home_medicine_tracker.application.common.PageResult;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.IntakeFilter;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class IntakePersistenceAdapter implements IntakePersistencePort {

    private final IntakeRecordJpaRepository repository;
    private final StockItemJpaRepository stockItemRepository;
    private final RegimenJpaRepository regimenRepository;
    private final IntakeRecordPersistenceMapper mapper;

    @Override
    public Optional<IntakeRecord> findById(Long id, Long userId) {
        return repository.findByIdAndStockItemMedicineUserId(id, userId).map(mapper::toDomain);
    }

    @Override
    public PageResult<IntakeRecord> search(IntakeFilter filter, PageRequest page, Long userId) {
        Page<IntakeRecordJpaEntity> found = repository.search(
                userId, filter.stockItemId(), filter.regimenId(), filter.from(), filter.to(),
                org.springframework.data.domain.PageRequest.of(
                        page.page(), page.size(), Sort.by(Sort.Direction.DESC, "takenAt")));

        return new PageResult<>(
                found.getContent().stream().map(mapper::toDomain).toList(),
                found.getNumber(), found.getSize(), found.getTotalElements());
    }

    @Override
    public IntakeRecord save(IntakeRecord record) {
        IntakeRecordJpaEntity entity = record.getId() == null
                ? new IntakeRecordJpaEntity()
                : repository.findLoadedById(record.getId())
                        .orElseThrow(() -> new NotFoundException("Запись приёма", record.getId()));

        if (record.getId() == null) {
            entity.setStockItem(stockItemRepository.getReferenceById(
                    record.getStockItem().getId()));
        }

        entity.setRegimen(record.getRegimen() == null
                ? null
                : regimenRepository.getReferenceById(record.getRegimen().getId()));

        mapper.applyToJpa(record, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void deleteById(Long id, Long userId) {
        repository.deleteByIdAndStockItemMedicineUserId(id, userId);
    }

    @Override
    public Map<Long, Long> countByRegimenBetween(Collection<Long> regimenIds,
                                                 Instant from, Instant to, Long userId) {
        if (regimenIds.isEmpty()) {
            return Map.of();
        }
        return repository.countByRegimenBetween(userId, regimenIds, from, to).stream()
                .collect(Collectors.toMap(RegimenIntakeCount::regimenId,
                        RegimenIntakeCount::taken));
    }
}
