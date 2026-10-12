package org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.ilestegor.home_medicine_tracker.domain.model.MedicineForm;

@Entity
@Table(name = "medicine")
@Getter
@Setter
public class MedicineJpaEntity extends BaseJpaEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String inn;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private MedicineForm form;

    @Column(length = 50)
    private String dosage;

    @Column(length = 200)
    private String manufacturer;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(nullable = false, length = 20)
    private String unit;
}
