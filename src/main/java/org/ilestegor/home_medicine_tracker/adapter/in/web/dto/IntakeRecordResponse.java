package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;

import java.math.BigDecimal;
import java.time.Instant;

public record IntakeRecordResponse(
        Long id,
        MedicineSummary medicine,
        Long stockItemId,
        BigDecimal stockItemQuantityAfter,
        Long regimenId,
        boolean byRegimen,
        BigDecimal amount,
        Instant takenAt,
        String note,
        Instant createdAt,
        Instant updatedAt
) {

    public static IntakeRecordResponse from(IntakeRecord r) {
        var stock = r.getStockItem();
        return new IntakeRecordResponse(
                r.getId(),
                MedicineSummary.from(stock.getMedicine()),
                stock.getId(),
                stock.getQuantity(),
                r.getRegimen() == null ? null : r.getRegimen().getId(),
                r.isByRegimen(),
                r.getAmount(),
                r.getTakenAt(),
                r.getNote(),
                r.getCreatedAt(),
                r.getUpdatedAt());
    }
}
