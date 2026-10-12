package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.ilestegor.home_medicine_tracker.domain.model.MedicineForm;

import java.time.Instant;

public record MedicineResponse(
        Long id,
        String name,
        String inn,
        MedicineForm form,
        String dosage,
        String manufacturer,
        String notes,
        String unit,
        Instant createdAt,
        Instant updatedAt
) {

    public static MedicineResponse from(Medicine m) {
        return new MedicineResponse(
                m.getId(), m.getName(), m.getInn(), m.getForm(), m.getDosage(),
                m.getManufacturer(), m.getNotes(), m.getUnit(),
                m.getCreatedAt(), m.getUpdatedAt());
    }
}
