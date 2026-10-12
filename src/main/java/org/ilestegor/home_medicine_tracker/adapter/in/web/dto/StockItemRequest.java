package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.AddStockItemCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.UpdateStockItemCommand;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockItemRequest(

        @NotNull(message = "препарат обязателен")
        Long medicineId,

        @NotNull(message = "количество обязательно")
        @DecimalMin(value = "0.00", message = "количество не может быть отрицательным")
        @Digits(integer = 8, fraction = 2, message = "не более 8 цифр и 2 знаков после запятой")
        BigDecimal quantity,

        @DecimalMin(value = "0.01", message = "должно быть больше нуля")
        @Digits(integer = 8, fraction = 2, message = "не более 8 цифр и 2 знаков после запятой")
        BigDecimal initialQuantity,

        @NotNull(message = "срок годности обязателен")
        LocalDate expiresAt,

        LocalDate openedAt
) {

    public AddStockItemCommand toAddCommand() {
        return new AddStockItemCommand(medicineId, quantity, initialQuantity, expiresAt, openedAt);
    }

    public UpdateStockItemCommand toUpdateCommand(Long id) {
        return new UpdateStockItemCommand(id, medicineId, quantity, initialQuantity,
                expiresAt, openedAt);
    }
}
