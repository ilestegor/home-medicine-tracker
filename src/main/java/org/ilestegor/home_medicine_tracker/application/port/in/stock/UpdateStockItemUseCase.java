package org.ilestegor.home_medicine_tracker.application.port.in.stock;

import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.UpdateStockItemCommand;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;

public interface UpdateStockItemUseCase {

    StockItem update(UpdateStockItemCommand command);
}
