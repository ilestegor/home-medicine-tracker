package org.ilestegor.home_medicine_tracker.application.port.in.stock.command;

import org.ilestegor.home_medicine_tracker.domain.model.StockStatus;

public record StockFilter(Long medicineId, StockStatus status) {

    public static StockFilter none() {
        return new StockFilter(null, null);
    }
}
