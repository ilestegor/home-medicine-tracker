package org.ilestegor.home_medicine_tracker.application.port.out;

import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.StockFilter;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockItemPersistencePort {

    Optional<StockItem> findById(Long id, Long userId);

    default StockItem requireById(Long id, Long userId) {
        return findById(id, userId).orElseThrow(() -> new NotFoundException("Упаковка", id));
    }

    List<StockItem> search(StockFilter filter, Long userId);

    StockItem save(StockItem stockItem);

    void deleteById(Long id, Long userId);

    List<StockItem> findActiveExpiringUntil(LocalDate deadline, Long userId);

    List<StockItem> findActiveExpiredBefore(LocalDate today, Long userId);

    List<StockItem> findActiveBelow(BigDecimal threshold, Long userId);

    Map<Long, BigDecimal> sumAvailableByMedicine(Collection<Long> medicineIds, LocalDate today, Long userId);

    int countActive(Long userId);
}
