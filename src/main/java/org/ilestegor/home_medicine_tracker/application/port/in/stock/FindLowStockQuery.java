package org.ilestegor.home_medicine_tracker.application.port.in.stock;

import org.ilestegor.home_medicine_tracker.domain.model.StockItem;

import java.math.BigDecimal;
import java.util.List;

public interface FindLowStockQuery {

    List<StockItem> below(BigDecimal threshold);
}
