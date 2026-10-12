package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.ilestegor.home_medicine_tracker.domain.model.MedicineForm;

public record MedicineSummary(
        Long id,
        String name,
        String dosage,
        MedicineForm form,
        String unit
) {

    public static MedicineSummary from(Medicine m) {
        return new MedicineSummary(m.getId(), m.getName(), m.getDosage(), m.getForm(), m.getUnit());
    }
}
