package org.ilestegor.home_medicine_tracker.application.service.stock;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.UpdateStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.UpdateStockItemCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class UpdateStockItemService implements UpdateStockItemUseCase {

    private final StockItemPersistencePort stockItems;
    private final StockItemAssembler assembler;
    private final CurrentUserPort currentUser;

    @Override
    public StockItem update(UpdateStockItemCommand command) {
        Long userId = currentUser.requireId();
        StockItem item = stockItems.requireById(command.id(), userId);

        if (!item.getMedicine().getId().equals(command.medicineId())) {
            throw new BusinessRuleException(
                    "Препарат у существующей упаковки изменить нельзя — создай новую упаковку");
        }

        BigDecimal quantityBefore = item.getQuantity();
        assembler.apply(command, item);
        StockItem saved = stockItems.save(item);

        if (saved.getQuantity().compareTo(quantityBefore) != 0) {
            log.atInfo()
                    .setMessage("Остаток упаковки скорректирован вручную")
                    .addKeyValue("event", "stock.quantity_corrected")
                    .addKeyValue("userId", userId)
                    .addKeyValue("stockItemId", saved.getId())
                    .addKeyValue("quantityBefore", quantityBefore)
                    .addKeyValue("quantityAfter", saved.getQuantity())
                    .log();
        }
        return saved;
    }
}
