package org.ilestegor.home_medicine_tracker.application.service.stock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.FindExpiringStockQuery;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindExpiringStockService implements FindExpiringStockQuery {

    private final StockItemPersistencePort stockItems;
    private final ClockPort clock;
    private final CurrentUserPort currentUser;

    @Override
    public List<StockItem> within(int days) {
        return stockItems.findActiveExpiringUntil(clock.today().plusDays(days), currentUser.requireId());
    }
}
