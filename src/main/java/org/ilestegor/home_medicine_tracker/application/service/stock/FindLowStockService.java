package org.ilestegor.home_medicine_tracker.application.service.stock;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.FindLowStockQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindLowStockService implements FindLowStockQuery {

    private final StockItemPersistencePort stockItems;
    private final CurrentUserPort currentUser;

    @Override
    public List<StockItem> below(BigDecimal threshold) {
        return stockItems.findActiveBelow(threshold, currentUser.requireId());
    }
}
