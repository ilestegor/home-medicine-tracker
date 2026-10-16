package org.ilestegor.home_medicine_tracker.application.service.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.AddStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.AddStockItemCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        Long userId = currentUser.requireId();
        Medicine medicine = medicines.requireById(command.medicineId(), userId);
        StockItem saved = stockItems.save(assembler.fromCommand(command, medicine));

        log.atInfo()
                .setMessage("Упаковка добавлена")
                .addKeyValue("event", "stock.added")
                .addKeyValue("userId", userId)
                .addKeyValue("stockItemId", saved.getId())
                .addKeyValue("medicineId", medicine.getId())
                .addKeyValue("quantity", saved.getQuantity())
                .addKeyValue("initialQuantity", saved.getInitialQuantity())
                .addKeyValue("expiresAt", saved.getExpiresAt())
                .log();
        return saved;
    }
}
