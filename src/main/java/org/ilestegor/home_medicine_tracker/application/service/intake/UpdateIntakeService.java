package org.ilestegor.home_medicine_tracker.application.service.intake;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.UpdateIntakeUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.UpdateIntakeCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class UpdateIntakeService implements UpdateIntakeUseCase {

    private final IntakePersistencePort intakes;
    private final StockItemPersistencePort stockItems;
    private final IntakeRules rules;
    private final CurrentUserPort currentUser;

    @Override
    public IntakeRecord update(UpdateIntakeCommand command) {
        Long userId = currentUser.requireId();
        IntakeRecord record = intakes.requireById(command.id(), userId);
        var stock = record.getStockItem();

        if (!stock.getId().equals(command.stockItemId())) {
            throw new BusinessRuleException(
                    "Перенести приём в другую упаковку нельзя — удали эту запись и создай новую");
        }

        BigDecimal amountBefore = record.getAmount();
        stock.restore(amountBefore);
        stock.withdraw(command.amount());
        stockItems.save(stock);

        record.setRegimen(rules.resolveRegimen(command.regimenId(), stock, userId));
        record.setAmount(command.amount());
        record.setTakenAt(rules.resolveTakenAt(command.takenAt(), record.getTakenAt()));
        record.setNote(command.note());

        IntakeRecord updated = intakes.save(record);

        log.atInfo()
                .setMessage("Приём изменён")
                .addKeyValue("event", "intake.updated")
                .addKeyValue("userId", userId)
                .addKeyValue("intakeId", updated.getId())
                .addKeyValue("stockItemId", stock.getId())
                .addKeyValue("amountBefore", amountBefore)
                .addKeyValue("amountAfter", command.amount())
                .addKeyValue("quantityLeft", stock.getQuantity())
                .addKeyValue("stockStatus", stock.getStatus())
                .log();
        return updated;
    }
}
