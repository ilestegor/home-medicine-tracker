package org.ilestegor.home_medicine_tracker.application.service.stock;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.UpdateStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.UpdateStockItemCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class UpdateStockItemService implements UpdateStockItemUseCase {

    private final StockItemPersistencePort stockItems;
    private final StockItemAssembler assembler;
    private final CurrentUserPort currentUser;

    @Override
    public StockItem update(UpdateStockItemCommand command) {
        StockItem item = stockItems.requireById(command.id(), currentUser.requireId());

        if (!item.getMedicine().getId().equals(command.medicineId())) {
            throw new BusinessRuleException(
                    "Препарат у существующей упаковки изменить нельзя — создай новую упаковку");
        }
        assembler.apply(command, item);
        return stockItems.save(item);
    }
}
