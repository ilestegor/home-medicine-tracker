package org.ilestegor.home_medicine_tracker.application.port.in.stock;

import org.ilestegor.home_medicine_tracker.domain.model.StockItem;

import java.util.List;

public interface FindExpiredStockQuery {

    List<StockItem> expired();
}
