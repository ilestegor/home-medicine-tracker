package org.ilestegor.home_medicine_tracker.application.service.stock;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.DeleteStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class DeleteStockItemService implements DeleteStockItemUseCase {

    private final StockItemPersistencePort stockItems;
    private final CurrentUserPort currentUser;

    @Override
    public void delete(Long id) {
        Long userId = currentUser.requireId();
        stockItems.requireById(id, userId);
        stockItems.deleteById(id, userId);
    }
}
