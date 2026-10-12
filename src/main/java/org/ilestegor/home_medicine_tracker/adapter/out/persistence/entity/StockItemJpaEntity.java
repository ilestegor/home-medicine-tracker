package org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.ilestegor.home_medicine_tracker.domain.model.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_item")
@Getter
@Setter
public class StockItemJpaEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private MedicineJpaEntity medicine;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "initial_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal initialQuantity;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "opened_at")
    private LocalDate openedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockStatus status = StockStatus.ACTIVE;

    @Version
    @Column(nullable = false)
    private Long version;
}
