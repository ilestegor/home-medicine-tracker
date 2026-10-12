package org.ilestegor.home_medicine_tracker.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.MedicineJpaEntity;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper.MedicinePersistenceMapper;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository.MedicineJpaRepository;
import org.ilestegor.home_medicine_tracker.application.port.out.MedicinePersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class MedicinePersistenceAdapter implements MedicinePersistencePort {

    private final MedicineJpaRepository repository;
    private final MedicinePersistenceMapper mapper;

    @Override
    public Optional<Medicine> findById(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId).map(mapper::toDomain);
    }

    @Override
    public List<Medicine> search(String query, Long userId) {
        List<MedicineJpaEntity> found = (query == null || query.isBlank())
                ? repository.findByUserIdOrderByNameAsc(userId)
                : repository.search(userId, query.strip());
        return found.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Medicine save(Medicine medicine) {
        MedicineJpaEntity entity = medicine.getId() == null
                ? new MedicineJpaEntity()
                : repository.findById(medicine.getId())
                        .orElseThrow(() -> new NotFoundException("Препарат", medicine.getId()));

        mapper.applyToJpa(medicine, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void deleteById(Long id, Long userId) {
        repository.deleteByIdAndUserId(id, userId);
    }

    @Override
    public long count(Long userId) {
        return repository.countByUserId(userId);
    }
}
