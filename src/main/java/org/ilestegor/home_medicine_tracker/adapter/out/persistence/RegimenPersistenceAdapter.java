package org.ilestegor.home_medicine_tracker.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.RegimenJpaEntity;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper.RegimenPersistenceMapper;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.MedicineJpaRepository;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.RegimenJpaRepository;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.RegimenFilter;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class RegimenPersistenceAdapter implements RegimenPersistencePort {

    private final RegimenJpaRepository repository;
    private final MedicineJpaRepository medicineRepository;
    private final RegimenPersistenceMapper mapper;

    @Override
    public Optional<Regimen> findById(Long id, Long userId) {
        return repository.findByIdAndMedicineUserId(id, userId).map(mapper::toDomain);
    }

    @Override
    public List<Regimen> search(RegimenFilter filter, Long userId) {
        return toDomain(repository.search(userId, filter.medicineId(), filter.active()));
    }

    @Override
    public Regimen save(Regimen regimen) {
        RegimenJpaEntity entity = regimen.getId() == null
                ? new RegimenJpaEntity()
                : repository.findWithMedicineById(regimen.getId())
                        .orElseThrow(() -> new NotFoundException("Схема приёма", regimen.getId()));

        if (regimen.getId() == null) {
            entity.setMedicine(medicineRepository.getReferenceById(
                    regimen.getMedicine().getId()));
        }
        mapper.applyToJpa(regimen, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void deleteById(Long id, Long userId) {
        repository.deleteByIdAndMedicineUserId(id, userId);
    }

    @Override
    public List<Regimen> findEffectiveOn(LocalDate date, Long userId) {
        return toDomain(repository.findEffectiveOn(userId, date));
    }

    private List<Regimen> toDomain(List<RegimenJpaEntity> entities) {
        return entities.stream().map(mapper::toDomain).toList();
    }
}
