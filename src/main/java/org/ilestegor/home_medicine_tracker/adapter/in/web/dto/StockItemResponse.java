package org.ilestegor.home_medicine_tracker.adapter.in.web.dto;

import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.ilestegor.home_medicine_tracker.domain.model.StockStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record StockItemResponse(
        Long id,
        MedicineSummary medicine,
        BigDecimal quantity,
        BigDecimal initialQuantity,
        LocalDate expiresAt,
        long daysUntilExpiry,
        boolean expired,
        LocalDate openedAt,
        StockStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static StockItemResponse from(StockItem s, LocalDate today) {
        return new StockItemResponse(
                s.getId(),
                MedicineSummary.from(s.getMedicine()),
                s.getQuantity(),
                s.getInitialQuantity(),
                s.getExpiresAt(),
                ChronoUnit.DAYS.between(today, s.getExpiresAt()),
                s.isExpiredOn(today),
                s.getOpenedAt(),
                s.getStatus(),
                s.getCreatedAt(),
                s.getUpdatedAt());
    }
}
