package org.ilestegor.home_medicine_tracker.application.service.stock;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.AddStockItemCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.UpdateStockItemCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
class StockItemAssembler {

    private final ClockPort clock;

    StockItem fromCommand(AddStockItemCommand c, Medicine medicine) {
        requireSaneOpenedAt(c.openedAt());
        requireQuantityFitsPack(c.quantity(), c.initialQuantity());

        return StockItem.builder()
                .medicine(medicine)
                .quantity(c.quantity())
                .initialQuantity(c.initialQuantity())
                .expiresAt(c.expiresAt())
                .openedAt(c.openedAt())
                .build();
    }

    void apply(UpdateStockItemCommand c, StockItem target) {
        requireSaneOpenedAt(c.openedAt());
        requireQuantityFitsPack(c.quantity(), c.initialQuantity());

        target.setInitialQuantity(c.initialQuantity());
        target.correctQuantity(c.quantity());
        target.setExpiresAt(c.expiresAt());
        target.setOpenedAt(c.openedAt());
    }

    private void requireQuantityFitsPack(java.math.BigDecimal quantity,
                                         java.math.BigDecimal initial) {
        if (quantity.compareTo(initial) > 0) {
            throw new BusinessRuleException(
                    "Остаток (%s) не может превышать изначальное количество (%s)"
                            .formatted(quantity.toPlainString(), initial.toPlainString()));
        }
    }

    private void requireSaneOpenedAt(LocalDate openedAt) {
        if (openedAt != null && openedAt.isAfter(clock.today())) {
            throw new BusinessRuleException("Дата вскрытия не может быть в будущем");
        }
    }
}
