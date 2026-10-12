package org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository;

import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.RegimenJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegimenJpaRepository extends JpaRepository<RegimenJpaEntity, Long> {

    @EntityGraph(attributePaths = "medicine")
    Optional<RegimenJpaEntity> findByIdAndMedicineUserId(Long id, Long userId);

    @EntityGraph(attributePaths = "medicine")
    Optional<RegimenJpaEntity> findWithMedicineById(Long id);

    @EntityGraph(attributePaths = "medicine")
    @Query("""
            select r from RegimenJpaEntity r
            where r.medicine.userId = :userId
              and (:medicineId is null or r.medicine.id = :medicineId)
              and (:active is null or r.active = :active)
            order by r.startsOn desc
            """)
    List<RegimenJpaEntity> search(@Param("userId") Long userId,
                                  @Param("medicineId") Long medicineId,
                                  @Param("active") Boolean active);

    void deleteByIdAndMedicineUserId(Long id, Long userId);

    @Query("""
            select r from RegimenJpaEntity r
            join fetch r.medicine m
            where m.userId = :userId
              and r.active = true
              and r.startsOn <= :date
              and (r.endsOn is null or r.endsOn >= :date)
            order by m.name
            """)
    List<RegimenJpaEntity> findEffectiveOn(@Param("userId") Long userId, @Param("date") LocalDate date);
}
