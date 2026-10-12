package org.ilestegor.home_medicine_tracker.application.port.in.stock;

import org.ilestegor.home_medicine_tracker.domain.model.StockItem;

public interface GetStockItemQuery {

    StockItem byId(Long id);
}
