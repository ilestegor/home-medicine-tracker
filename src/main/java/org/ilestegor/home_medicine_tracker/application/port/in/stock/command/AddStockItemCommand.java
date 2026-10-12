package org.ilestegor.home_medicine_tracker.application.port.in.stock.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddStockItemCommand(
        Long medicineId,
        BigDecimal quantity,
        BigDecimal initialQuantity,
        LocalDate expiresAt,
        LocalDate openedAt
) {
    public AddStockItemCommand {
        initialQuantity = initialQuantity == null ? quantity : initialQuantity;
    }
}
