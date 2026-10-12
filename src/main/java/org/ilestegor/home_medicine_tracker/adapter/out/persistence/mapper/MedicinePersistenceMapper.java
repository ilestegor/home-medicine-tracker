package org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper;

import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.MedicineJpaEntity;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.springframework.stereotype.Component;

@Component
public class MedicinePersistenceMapper {

    public Medicine toDomain(MedicineJpaEntity e) {
        return Medicine.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .name(e.getName())
                .inn(e.getInn())
                .form(e.getForm())
                .dosage(e.getDosage())
                .manufacturer(e.getManufacturer())
                .notes(e.getNotes())
                .unit(e.getUnit())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public void applyToJpa(Medicine m, MedicineJpaEntity target) {
        if (target.getUserId() == null) {
            target.setUserId(m.getUserId());
        }
        target.setName(m.getName());
        target.setInn(m.getInn());
        target.setForm(m.getForm());
        target.setDosage(m.getDosage());
        target.setManufacturer(m.getManufacturer());
        target.setNotes(m.getNotes());
        target.setUnit(m.getUnit());
    }
}
