package org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository;

import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.IntakeRecordJpaEntity;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.projection.RegimenIntakeCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IntakeRecordJpaRepository extends JpaRepository<IntakeRecordJpaEntity, Long> {

    @EntityGraph(attributePaths = {"stockItem", "stockItem.medicine", "regimen"})
    Optional<IntakeRecordJpaEntity> findByIdAndStockItemMedicineUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"stockItem", "stockItem.medicine", "regimen"})
    Optional<IntakeRecordJpaEntity> findLoadedById(Long id);

    @EntityGraph(attributePaths = {"stockItem", "stockItem.medicine", "regimen"})
    @Query(value = """
            select i from IntakeRecordJpaEntity i
            where i.stockItem.medicine.userId = :userId
              and (:stockItemId is null or i.stockItem.id = :stockItemId)
              and (:regimenId is null or i.regimen.id = :regimenId)
              and (cast(:from as Instant) is null or i.takenAt >= :from)
              and (cast(:to as Instant) is null or i.takenAt < :to)
            """,
            countQuery = """
            select count(i) from IntakeRecordJpaEntity i
            where i.stockItem.medicine.userId = :userId
              and (:stockItemId is null or i.stockItem.id = :stockItemId)
              and (:regimenId is null or i.regimen.id = :regimenId)
              and (cast(:from as Instant) is null or i.takenAt >= :from)
              and (cast(:to as Instant) is null or i.takenAt < :to)
            """)
    Page<IntakeRecordJpaEntity> search(@Param("userId") Long userId,
                                       @Param("stockItemId") Long stockItemId,
                                       @Param("regimenId") Long regimenId,
                                       @Param("from") Instant from,
                                       @Param("to") Instant to,
                                       Pageable pageable);

    void deleteByIdAndStockItemMedicineUserId(Long id, Long userId);

    @Query("""
            select new org.ilestegor.home_medicine_tracker.adapter.out.persistence.projection.RegimenIntakeCount(
                       r.id, count(i))
            from IntakeRecordJpaEntity i
            join i.regimen r
            where r.medicine.userId = :userId
              and r.id in :regimenIds
              and i.takenAt >= :from
              and i.takenAt < :to
            group by r.id
            """)
    List<RegimenIntakeCount> countByRegimenBetween(@Param("userId") Long userId,
                                                   @Param("regimenIds") Collection<Long> regimenIds,
                                                   @Param("from") Instant from,
                                                   @Param("to") Instant to);
}
