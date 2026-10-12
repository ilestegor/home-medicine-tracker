package org.ilestegor.home_medicine_tracker.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class Medicine {

    private final Long id;

    private final Long userId;

    @Setter
    private String name;

    @Setter
    private String inn;

    @Setter
    private MedicineForm form;

    @Setter
    private String dosage;

    @Setter
    private String manufacturer;

    @Setter
    private String notes;

    @Setter
    private String unit;

    private final Instant createdAt;
    private final Instant updatedAt;
}
