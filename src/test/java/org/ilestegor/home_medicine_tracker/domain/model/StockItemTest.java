package org.ilestegor.home_medicine_tracker.domain.model;

import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockItemTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 3, 15);
    private static StockItem pack(String quantity, String initial) {
        Medicine medicine = Medicine.builder()
                .name("Тестовый препарат")
                .unit("шт")
                .build();
        return StockItem.builder()
                .medicine(medicine)
                .quantity(new BigDecimal(quantity))
                .initialQuantity(new BigDecimal(initial))
                .expiresAt(TODAY.plusMonths(6))
                .build();
    }
    @Nested
    @DisplayName("Списание")
    class Withdraw {

        @Test
        void reducesQuantity() {
            StockItem item = pack("10", "10");
            item.withdraw(new BigDecimal("3"));
            assertThat(item.getQuantity()).isEqualByComparingTo("7");
            assertThat(item.getStatus()).isEqualTo(StockStatus.ACTIVE);
        }

        @Test
        void marksFinishedWhenQuantityReachesZero() {
            StockItem item = pack("2", "10");
            item.withdraw(new BigDecimal("2"));
            assertThat(item.getQuantity()).isEqualByComparingTo("0");
            assertThat(item.getStatus()).isEqualTo(StockStatus.FINISHED);
        }

        @Test
        void rejectsWithdrawalExceedingQuantity() {
            StockItem item = pack("2", "10");

            assertThatThrownBy(() -> item.withdraw(new BigDecimal("2.5")))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("осталось 2 шт");

            assertThat(item.getQuantity())
                    .as("остаток не должен измениться после отказа")
                    .isEqualByComparingTo("2");
        }
        @Test
        void rejectsWithdrawalFromDiscardedPack() {
            StockItem item = pack("10", "10");
            item.discard();
            assertThatThrownBy(() -> item.withdraw(BigDecimal.ONE))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("DISCARDED");
        }
        @Test
        void rejectsWithdrawalFromFinishedPack() {
            StockItem item = pack("1", "10");
            item.withdraw(BigDecimal.ONE);
            assertThatThrownBy(() -> item.withdraw(BigDecimal.ONE))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("FINISHED");
        }
    }
    @Nested
    @DisplayName("Возврат")
    class Restore {

        @Test
        void restoresQuantity() {
            StockItem item = pack("10", "10");
            item.withdraw(new BigDecimal("4"));
            item.restore(new BigDecimal("4"));
            assertThat(item.getQuantity()).isEqualByComparingTo("10");
        }
        @Test
        void reopensFinishedPack() {
            StockItem item = pack("1", "10");
            item.withdraw(BigDecimal.ONE);
            item.restore(BigDecimal.ONE);
            assertThat(item.getStatus()).isEqualTo(StockStatus.ACTIVE);
            assertThat(item.getQuantity()).isEqualByComparingTo("1");
        }

        @Test
        void rejectsRestoreAboveInitialQuantity() {
            StockItem item = pack("10", "10");

            assertThatThrownBy(() -> item.restore(BigDecimal.ONE))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("изначальное количество");
        }
    }
    @Nested
    @DisplayName("Правка приёма: вернуть и списать заново")
    class Rewithdraw {

        @Test
        void appliesIncreasedIntakeAmount() {
            StockItem item = pack("10", "10");
            item.withdraw(new BigDecimal("2"));
            item.restore(new BigDecimal("2"));
            item.withdraw(new BigDecimal("5"));
            assertThat(item.getQuantity()).isEqualByComparingTo("5");
        }
        @Test
        void rejectsEditExceedingAvailableQuantity() {
            StockItem item = pack("10", "10");
            item.withdraw(new BigDecimal("2"));
            item.restore(new BigDecimal("2"));
            assertThatThrownBy(() -> item.withdraw(new BigDecimal("11")))
                    .isInstanceOf(BusinessRuleException.class);
        }
    }
    @Nested
    @DisplayName("Выброс, правка остатка и срок годности")
    class DiscardAndExpiry {

        @Test
        void rejectsSecondDiscard() {
            StockItem item = pack("10", "10");
            item.discard();
            assertThatThrownBy(item::discard)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("уже выброшена");
        }
        @Test
        void rejectsQuantityCorrectionAboveInitial() {
            StockItem item = pack("10", "10");

            assertThatThrownBy(() -> item.correctQuantity(new BigDecimal("11")))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("не может превышать");
        }
        @Test
        void treatsEarlierExpiryDateAsExpired() {
            StockItem item = pack("10", "10");
            item.setExpiresAt(TODAY.minusDays(1));
            assertThat(item.isExpiredOn(TODAY)).isTrue();
        }
        @Test
        void isNotExpiredOnLastDayOfShelfLife() {
            StockItem item = pack("10", "10");
            item.setExpiresAt(TODAY);
            assertThat(item.isExpiredOn(TODAY))
                    .as("срок годности «до 15 марта» включает 15 марта")
                    .isFalse();
        }
    }
}
