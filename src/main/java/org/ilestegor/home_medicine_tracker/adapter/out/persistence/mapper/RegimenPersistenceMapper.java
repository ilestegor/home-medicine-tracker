package org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.RegimenJpaEntity;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegimenPersistenceMapper {

    private final MedicinePersistenceMapper medicineMapper;

    public Regimen toDomain(RegimenJpaEntity e) {
        return Regimen.builder()
                .id(e.getId())
                .medicine(medicineMapper.toDomain(e.getMedicine()))
                .doseAmount(e.getDoseAmount())
                .timesPerDay(e.getTimesPerDay())
                .startsOn(e.getStartsOn())
                .endsOn(e.getEndsOn())
                .instructions(e.getInstructions())
                .active(e.isActive())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public void applyToJpa(Regimen r, RegimenJpaEntity target) {
        target.setDoseAmount(r.getDoseAmount());
        target.setTimesPerDay(r.getTimesPerDay());
        target.setStartsOn(r.getStartsOn());
        target.setEndsOn(r.getEndsOn());
        target.setInstructions(r.getInstructions());
        target.setActive(r.isActive());
    }
}
