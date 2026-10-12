package org.ilestegor.home_medicine_tracker.application.service.intake;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.DeleteIntakeUseCase;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class DeleteIntakeService implements DeleteIntakeUseCase {

    private final IntakePersistencePort intakes;
    private final StockItemPersistencePort stockItems;
    private final CurrentUserPort currentUser;

    @Override
    public void delete(Long id) {
        Long userId = currentUser.requireId();
        IntakeRecord record = intakes.requireById(id, userId);

        var stock = record.getStockItem();
        stock.restore(record.getAmount());
        stockItems.save(stock);

        intakes.deleteById(id, userId);
    }
}
