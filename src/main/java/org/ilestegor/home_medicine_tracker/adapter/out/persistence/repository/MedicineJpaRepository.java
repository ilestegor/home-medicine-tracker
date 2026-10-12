package org.ilestegor.home_medicine_tracker.adapter.out.persistence.repository;

import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.MedicineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicineJpaRepository extends JpaRepository<MedicineJpaEntity, Long> {

    Optional<MedicineJpaEntity> findByIdAndUserId(Long id, Long userId);

    List<MedicineJpaEntity> findByUserIdOrderByNameAsc(Long userId);

    @Query("""
            select m from MedicineJpaEntity m
            where m.userId = :userId
              and (lower(m.name) like lower(concat('%', :q, '%'))
                   or lower(m.inn) like lower(concat('%', :q, '%')))
            order by m.name
            """)
    List<MedicineJpaEntity> search(@Param("userId") Long userId, @Param("q") String q);

    void deleteByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}
