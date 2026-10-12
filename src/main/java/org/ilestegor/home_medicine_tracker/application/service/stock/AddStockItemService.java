package org.ilestegor.home_medicine_tracker.application.service.stock;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.AddStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.AddStockItemCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class AddStockItemService implements AddStockItemUseCase {

    private final StockItemPersistencePort stockItems;
    private final MedicinePersistencePort medicines;
    private final StockItemAssembler assembler;
    private final CurrentUserPort currentUser;

    @Override
    public StockItem add(AddStockItemCommand command) {
        Medicine medicine = medicines.requireById(command.medicineId(), currentUser.requireId());
        return stockItems.save(assembler.fromCommand(command, medicine));
    }
}
