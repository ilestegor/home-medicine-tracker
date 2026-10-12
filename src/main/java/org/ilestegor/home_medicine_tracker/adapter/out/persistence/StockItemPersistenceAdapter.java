package org.ilestegor.home_medicine_tracker.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.StockItemJpaEntity;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper.StockItemPersistenceMapper;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.projection.MedicineStockTotal;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.MedicineJpaRepository;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.StockItemJpaRepository;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.StockFilter;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.ilestegor.home_medicine_tracker.domain.model.StockStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class StockItemPersistenceAdapter implements StockItemPersistencePort {

    private final StockItemJpaRepository repository;
    private final MedicineJpaRepository medicineRepository;
    private final StockItemPersistenceMapper mapper;

    @Override
    public Optional<StockItem> findById(Long id, Long userId) {
        return repository.findByIdAndMedicineUserId(id, userId).map(mapper::toDomain);
    }

    @Override
    public List<StockItem> search(StockFilter filter, Long userId) {
        return toDomain(repository.search(userId, filter.medicineId(), filter.status()));
    }

    @Override
    public StockItem save(StockItem stockItem) {
        StockItemJpaEntity entity = stockItem.getId() == null
                ? new StockItemJpaEntity()
                : repository.findWithMedicineById(stockItem.getId())
                        .orElseThrow(() -> new NotFoundException("Упаковка", stockItem.getId()));

        if (stockItem.getId() == null) {
            entity.setMedicine(medicineRepository.getReferenceById(
                    stockItem.getMedicine().getId()));
        }
        mapper.applyToJpa(stockItem, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void deleteById(Long id, Long userId) {
        repository.deleteByIdAndMedicineUserId(id, userId);
    }

    @Override
    public List<StockItem> findActiveExpiringUntil(LocalDate deadline, Long userId) {
        return toDomain(repository
                .findByMedicineUserIdAndStatusAndExpiresAtLessThanEqualOrderByExpiresAtAsc(
                        userId, StockStatus.ACTIVE, deadline));
    }

    @Override
    public List<StockItem> findActiveExpiredBefore(LocalDate today, Long userId) {
        return toDomain(repository
                .findByMedicineUserIdAndStatusAndExpiresAtBeforeOrderByExpiresAtAsc(
                        userId, StockStatus.ACTIVE, today));
    }

    @Override
    public List<StockItem> findActiveBelow(BigDecimal threshold, Long userId) {
        return toDomain(repository
                .findByMedicineUserIdAndStatusAndQuantityLessThanOrderByQuantityAsc(
                        userId, StockStatus.ACTIVE, threshold));
    }

    @Override
    public Map<Long, BigDecimal> sumAvailableByMedicine(Collection<Long> medicineIds,
                                                        LocalDate today, Long userId) {
        if (medicineIds.isEmpty()) {
            return Map.of();
        }
        return repository.sumAvailableByMedicine(userId, medicineIds, StockStatus.ACTIVE, today)
                .stream()
                .collect(Collectors.toMap(MedicineStockTotal::medicineId,
                        MedicineStockTotal::available));
    }

    @Override
    public int countActive(Long userId) {
        return repository.countByMedicineUserIdAndStatus(userId, StockStatus.ACTIVE);
    }

    private List<StockItem> toDomain(List<StockItemJpaEntity> entities) {
        return entities.stream().map(mapper::toDomain).toList();
    }
}
