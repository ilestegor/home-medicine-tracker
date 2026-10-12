package org.ilestegor.home_medicine_tracker.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
public class StockItem {

    private final Long id;

    private final Medicine medicine;

    private BigDecimal quantity;

    @Setter
    private BigDecimal initialQuantity;

    @Setter
    private LocalDate expiresAt;

    @Setter
    private LocalDate openedAt;

    @Builder.Default
    private StockStatus status = StockStatus.ACTIVE;

    private final Long version;
    private final Instant createdAt;
    private final Instant updatedAt;

    public String unit() {
        return medicine.getUnit();
    }

    public void withdraw(BigDecimal amount) {
        if (status != StockStatus.ACTIVE) {
            throw new BusinessRuleException(
                    "Упаковка в статусе %s — списать из неё нельзя".formatted(status));
        }
        if (quantity.compareTo(amount) < 0) {
            throw new BusinessRuleException(
                    "В упаковке осталось %s %s, списать %s нельзя".formatted(
                            quantity.stripTrailingZeros().toPlainString(), unit(),
                            amount.stripTrailingZeros().toPlainString()));
        }
        quantity = quantity.subtract(amount);
        if (quantity.signum() == 0) {
            status = StockStatus.FINISHED;
        }
    }

    public void restore(BigDecimal amount) {
        BigDecimal restored = quantity.add(amount);
        if (restored.compareTo(initialQuantity) > 0) {
            throw new BusinessRuleException(
                    "Возврат %s превысил бы изначальное количество упаковки (%s)".formatted(
                            amount.stripTrailingZeros().toPlainString(),
                            initialQuantity.stripTrailingZeros().toPlainString()));
        }
        quantity = restored;
        if (status == StockStatus.FINISHED) {
            status = StockStatus.ACTIVE;
        }
    }

    public void discard() {
        if (status == StockStatus.DISCARDED) {
            throw new BusinessRuleException("Упаковка уже выброшена");
        }
        status = StockStatus.DISCARDED;
    }

    public void correctQuantity(BigDecimal newQuantity) {
        if (newQuantity.compareTo(initialQuantity) > 0) {
            throw new BusinessRuleException(
                    "Остаток (%s) не может превышать изначальное количество (%s)".formatted(
                            newQuantity.toPlainString(), initialQuantity.toPlainString()));
        }
        quantity = newQuantity;
    }

    public boolean isExpiredOn(LocalDate date) {
        return expiresAt.isBefore(date);
    }
}
