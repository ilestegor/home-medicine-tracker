package org.ilestegor.home_medicine_tracker.application.service.intake;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.RecordIntakeUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.RecordIntakeCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.ilestegor.home_medicine_tracker.domain.model.StockStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class RecordIntakeService implements RecordIntakeUseCase {

    private final IntakePersistencePort intakes;
    private final StockItemPersistencePort stockItems;
    private final IntakeRules rules;
    private final CurrentUserPort currentUser;

    @Override
    public IntakeRecord record(RecordIntakeCommand command) {
        Long userId = currentUser.requireId();
        StockItem stock = stockItems.requireById(command.stockItemId(), userId);
        Regimen regimen = rules.resolveRegimen(command.regimenId(), stock, userId);
        Instant takenAt = rules.resolveTakenAt(command.takenAt(), rules.now());

        rules.requireNotExpired(stock);

        StockStatus statusBefore = stock.getStatus();
        stock.withdraw(command.amount());
        StockItem saved = stockItems.save(stock);

        IntakeRecord recorded = intakes.save(IntakeRecord.builder()
                .stockItem(saved)
                .regimen(regimen)
                .amount(command.amount())
                .takenAt(takenAt)
                .note(command.note())
                .build());

        log.atInfo()
                .setMessage("Приём записан")
                .addKeyValue("event", "intake.recorded")
                .addKeyValue("userId", userId)
                .addKeyValue("intakeId", recorded.getId())
                .addKeyValue("stockItemId", saved.getId())
                .addKeyValue("medicineId", saved.getMedicine().getId())
                .addKeyValue("regimenId", regimen != null ? regimen.getId() : null)
                .addKeyValue("amount", command.amount())
                .addKeyValue("quantityLeft", saved.getQuantity())
                .log();

        if (saved.getStatus() != statusBefore) {
            log.atInfo()
                    .setMessage("Упаковка сменила статус")
                    .addKeyValue("event", "stock.status_changed")
                    .addKeyValue("userId", userId)
                    .addKeyValue("stockItemId", saved.getId())
                    .addKeyValue("statusBefore", statusBefore)
                    .addKeyValue("statusAfter", saved.getStatus())
                    .log();
        }
        return recorded;
    }
}
