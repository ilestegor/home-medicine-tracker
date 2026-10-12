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

        stock.withdraw(command.amount());
        StockItem saved = stockItems.save(stock);

        IntakeRecord recorded = intakes.save(IntakeRecord.builder()
                .stockItem(saved)
                .regimen(regimen)
                .amount(command.amount())
                .takenAt(takenAt)
                .note(command.note())
                .build());

        log.info("Приём записан: userId={}, stockItemId={}, принято={}, остаток={}",
                userId, saved.getId(), command.amount(), saved.getQuantity());
        return recorded;
    }
}
