package org.ilestegor.home_medicine_tracker.application.service.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.DeleteStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class DeleteStockItemService implements DeleteStockItemUseCase {

    private final StockItemPersistencePort stockItems;
    private final CurrentUserPort currentUser;

    @Override
    public void delete(Long id) {
        Long userId = currentUser.requireId();
        StockItem item = stockItems.requireById(id, userId);
        stockItems.deleteById(id, userId);

        log.atInfo()
                .setMessage("Упаковка удалена")
                .addKeyValue("event", "stock.deleted")
                .addKeyValue("userId", userId)
                .addKeyValue("stockItemId", id)
                .addKeyValue("medicineId", item.getMedicine().getId())
                .addKeyValue("quantityLeft", item.getQuantity())
                .log();
    }
}
