package org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.IntakeRecordJpaEntity;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntakeRecordPersistenceMapper {

    private final StockItemPersistenceMapper stockItemMapper;
    private final RegimenPersistenceMapper regimenMapper;

    public IntakeRecord toDomain(IntakeRecordJpaEntity e) {
        return IntakeRecord.builder()
                .id(e.getId())
                .stockItem(stockItemMapper.toDomain(e.getStockItem()))
                .regimen(e.getRegimen() == null ? null : regimenMapper.toDomain(e.getRegimen()))
                .amount(e.getAmount())
                .takenAt(e.getTakenAt())
                .note(e.getNote())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public void applyToJpa(IntakeRecord r, IntakeRecordJpaEntity target) {
        target.setAmount(r.getAmount());
        target.setTakenAt(r.getTakenAt());
        target.setNote(r.getNote());
    }
}
