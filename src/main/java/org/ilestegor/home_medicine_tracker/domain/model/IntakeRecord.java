package org.ilestegor.home_medicine_tracker.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class IntakeRecord {

    private final Long id;

    private final StockItem stockItem;

    @Setter
    private Regimen regimen;

    @Setter
    private BigDecimal amount;

    @Setter
    private Instant takenAt;

    @Setter
    private String note;

    private final Instant createdAt;

    private final Instant updatedAt;

    public boolean isByRegimen() {
        return regimen != null;
    }
}
