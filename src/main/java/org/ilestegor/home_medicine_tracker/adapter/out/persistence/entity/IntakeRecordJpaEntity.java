package org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "intake_record")
@Getter
@Setter
public class IntakeRecordJpaEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_item_id", nullable = false)
    private StockItemJpaEntity stockItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regimen_id")
    private RegimenJpaEntity regimen;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "taken_at", nullable = false)
    private Instant takenAt;

    @Column(columnDefinition = "text")
    private String note;
}
