package org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository;

import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.StockItemJpaEntity;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.projection.MedicineStockTotal;
import org.ilestegor.home_medicine_tracker.domain.model.StockStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StockItemJpaRepository extends JpaRepository<StockItemJpaEntity, Long> {

    @EntityGraph(attributePaths = "medicine")
    Optional<StockItemJpaEntity> findByIdAndMedicineUserId(Long id, Long userId);

    @EntityGraph(attributePaths = "medicine")
    Optional<StockItemJpaEntity> findWithMedicineById(Long id);

    @EntityGraph(attributePaths = "medicine")
    @Query("""
            select s from StockItemJpaEntity s
            where s.medicine.userId = :userId
              and (:medicineId is null or s.medicine.id = :medicineId)
              and (:status is null or s.status = :status)
            order by s.expiresAt
            """)
    List<StockItemJpaEntity> search(@Param("userId") Long userId,
                                    @Param("medicineId") Long medicineId,
                                    @Param("status") StockStatus status);

    @EntityGraph(attributePaths = "medicine")
    List<StockItemJpaEntity> findByMedicineUserIdAndStatusAndExpiresAtLessThanEqualOrderByExpiresAtAsc(
            Long userId, StockStatus status, LocalDate deadline);

    @EntityGraph(attributePaths = "medicine")
    List<StockItemJpaEntity> findByMedicineUserIdAndStatusAndExpiresAtBeforeOrderByExpiresAtAsc(
            Long userId, StockStatus status, LocalDate today);

    @EntityGraph(attributePaths = "medicine")
    List<StockItemJpaEntity> findByMedicineUserIdAndStatusAndQuantityLessThanOrderByQuantityAsc(
            Long userId, StockStatus status, BigDecimal threshold);

    int countByMedicineUserIdAndStatus(Long userId, StockStatus status);

    void deleteByIdAndMedicineUserId(Long id, Long userId);

    @Query("""
            select new org.ilestegor.home_medicine_tracker.adapter.out.persistence.projection.MedicineStockTotal(
                       m.id, sum(s.quantity))
            from StockItemJpaEntity s
            join s.medicine m
            where m.userId = :userId
              and m.id in :medicineIds
              and s.status = :status
              and s.expiresAt >= :today
            group by m.id
            """)
    List<MedicineStockTotal> sumAvailableByMedicine(@Param("userId") Long userId,
                                                    @Param("medicineIds") Collection<Long> medicineIds,
                                                    @Param("status") StockStatus status,
                                                    @Param("today") LocalDate today);
}
