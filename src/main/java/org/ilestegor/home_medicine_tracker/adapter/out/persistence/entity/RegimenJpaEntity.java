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
import java.time.LocalDate;

@Entity
@Table(name = "regimen")
@Getter
@Setter
public class RegimenJpaEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private MedicineJpaEntity medicine;

    @Column(name = "dose_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal doseAmount;

    @Column(name = "times_per_day", nullable = false)
    private Short timesPerDay;

    @Column(name = "starts_on", nullable = false)
    private LocalDate startsOn;

    @Column(name = "ends_on")
    private LocalDate endsOn;

    @Column(columnDefinition = "text")
    private String instructions;

    @Column(nullable = false)
    private boolean active = true;
}
