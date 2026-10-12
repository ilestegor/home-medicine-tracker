package org.ilestegor.home_medicine_tracker.adapter.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.out.persistence.entity.StockItemJpaEntity;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockItemPersistenceMapper {

    private final MedicinePersistenceMapper medicineMapper;

    public StockItem toDomain(StockItemJpaEntity e) {
        return StockItem.builder()
                .id(e.getId())
                .medicine(medicineMapper.toDomain(e.getMedicine()))
                .quantity(e.getQuantity())
                .initialQuantity(e.getInitialQuantity())
                .expiresAt(e.getExpiresAt())
                .openedAt(e.getOpenedAt())
                .status(e.getStatus())
                .version(e.getVersion())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public void applyToJpa(StockItem s, StockItemJpaEntity target) {
        target.setQuantity(s.getQuantity());
        target.setInitialQuantity(s.getInitialQuantity());
        target.setExpiresAt(s.getExpiresAt());
        target.setOpenedAt(s.getOpenedAt());
        target.setStatus(s.getStatus());
    }
}
