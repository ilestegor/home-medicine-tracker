package org.ilestegor.home_medicine_tracker.application.service.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.DiscardStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class DiscardStockItemService implements DiscardStockItemUseCase {

    private final StockItemPersistencePort stockItems;
    private final CurrentUserPort currentUser;

    @Override
    public StockItem discard(Long id) {
        Long userId = currentUser.requireId();
        StockItem item = stockItems.requireById(id, userId);
        item.discard();
        StockItem discarded = stockItems.save(item);

        log.info("Упаковка списана: userId={}, stockItemId={}, остаток={}",
                userId, id, discarded.getQuantity());
        return discarded;
    }
}
