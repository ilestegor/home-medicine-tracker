package org.ilestegor.home_medicine_tracker.application.service.intake;

import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.RecordIntakeCommand;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.application.port.out.CurrentUserPort;
import org.ilestegor.home_medicine_tracker.application.port.out.IntakePersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.RegimenPersistencePort;
import org.ilestegor.home_medicine_tracker.application.port.out.StockItemPersistencePort;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;
import org.ilestegor.home_medicine_tracker.domain.model.Medicine;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecordIntakeServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 3, 15);
    private static final Instant NOW = TODAY.atTime(9, 0).toInstant(java.time.ZoneOffset.UTC);
    private static final Long USER_ID = 1L;
    private StubStock stockItems;
    private StubIntakes intakes;
    private StubRegimens regimens;
    private RecordIntakeService service;
    @BeforeEach
    void setUp() {
        stockItems = new StubStock();
        intakes = new StubIntakes();
        regimens = new StubRegimens();
        service = new RecordIntakeService(intakes, stockItems,
                new IntakeRules(regimens, new FixedClock()), () -> USER_ID);
    }
    private static Medicine medicine(long id, String name) {
        return Medicine.builder().id(id).name(name).unit("шт").build();
    }

    private static StockItem stock(long id, Medicine medicine, String quantity, LocalDate expires) {
        return StockItem.builder()
                .id(id).medicine(medicine)
                .quantity(new BigDecimal(quantity))
                .initialQuantity(new BigDecimal(quantity))
                .expiresAt(expires)
                .build();
    }
    private static Regimen regimen(long id, Medicine medicine) {
        return Regimen.builder()
                .id(id).medicine(medicine)
                .doseAmount(BigDecimal.ONE).timesPerDay((short) 3)
                .startsOn(TODAY.minusDays(1))
                .build();
    }
    @Test
    @DisplayName("Списывает остаток и сохраняет упаковку в той же операции")
    void withdrawsFromStockAndSavesPack() {
        Medicine nurofen = medicine(1L, "Нурофен");
        stockItems.put(stock(10L, nurofen, "10", TODAY.plusYears(1)));
        IntakeRecord saved = service.record(
                new RecordIntakeCommand(10L, null, new BigDecimal("2"), null, null));
        assertThat(stockItems.get(10L).getQuantity()).isEqualByComparingTo("8");
        assertThat(stockItems.savedCount).as("упаковка должна быть сохранена").isOne();
        assertThat(saved.getAmount()).isEqualByComparingTo("2");
        assertThat(saved.isByRegimen()).isFalse();
    }
    @Test
    @DisplayName("Время приёма по умолчанию — сейчас")
    void defaultsTakenAtToNow() {
        stockItems.put(stock(10L, medicine(1L, "Нурофен"), "10", TODAY.plusYears(1)));
        IntakeRecord saved = service.record(
                new RecordIntakeCommand(10L, null, BigDecimal.ONE, null, null));
        assertThat(saved.getTakenAt()).isEqualTo(NOW);
    }
    @Test
    @DisplayName("Время приёма в будущем отбивается")
    void rejectsFutureTakenAt() {
        stockItems.put(stock(10L, medicine(1L, "Нурофен"), "10", TODAY.plusYears(1)));
        assertThatThrownBy(() -> service.record(new RecordIntakeCommand(
                10L, null, BigDecimal.ONE, NOW.plus(1, ChronoUnit.HOURS), null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("в будущем");
    }
    @Test
    @DisplayName("Приём из просроченной упаковки отбивается, остаток не трогается")
    void rejectsIntakeFromExpiredPack() {
        stockItems.put(stock(10L, medicine(1L, "Нурофен"), "10", TODAY.minusDays(1)));
        assertThatThrownBy(() -> service.record(
                new RecordIntakeCommand(10L, null, BigDecimal.ONE, null, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("просрочена");
        assertThat(stockItems.get(10L).getQuantity()).isEqualByComparingTo("10");
        assertThat(stockItems.savedCount).isZero();
    }

    @Test
    @DisplayName("Ромб-инвариант: схема от другого препарата отбивается")
    void rejectsRegimenForDifferentMedicine() {
        Medicine nurofen = medicine(1L, "Нурофен");
        Medicine amoxicillin = medicine(2L, "Амоксициллин");
        stockItems.put(stock(10L, nurofen, "10", TODAY.plusYears(1)));
        regimens.put(regimen(50L, amoxicillin));
        assertThatThrownBy(() -> service.record(
                new RecordIntakeCommand(10L, 50L, BigDecimal.ONE, null, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Амоксициллин");
        assertThat(stockItems.get(10L).getQuantity())
                .as("отказ до списания — остаток цел")
                .isEqualByComparingTo("10");
    }
    @Test
    @DisplayName("Схема того же препарата принимается")
    void acceptsRegimenForSameMedicine() {
        Medicine nurofen = medicine(1L, "Нурофен");
        stockItems.put(stock(10L, nurofen, "10", TODAY.plusYears(1)));
        regimens.put(regimen(50L, nurofen));
        IntakeRecord saved = service.record(
                new RecordIntakeCommand(10L, 50L, BigDecimal.ONE, null, null));
        assertThat(saved.isByRegimen()).isTrue();
        assertThat(saved.getRegimen().getId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Списание больше остатка отбивается упаковкой")
    void rejectsAmountExceedingQuantity() {
        stockItems.put(stock(10L, medicine(1L, "Нурофен"), "3", TODAY.plusYears(1)));
        assertThatThrownBy(() -> service.record(
                new RecordIntakeCommand(10L, null, new BigDecimal("4"), null, null)))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(stockItems.savedCount).isZero();
    }

    private static class FixedClock implements ClockPort {
        @Override
        public Instant now() {
            return NOW;
        }
        @Override
        public LocalDate today() {
            return TODAY;
        }
    }
    private static class StubStock implements StockItemPersistencePort {
        private final java.util.Map<Long, StockItem> items = new java.util.HashMap<>();
        private int savedCount;

        void put(StockItem item) {
            items.put(item.getId(), item);
        }

        StockItem get(Long id) {
            return items.get(id);
        }

        @Override
        public Optional<StockItem> findById(Long id, Long userId) {
            return Optional.ofNullable(items.get(id));
        }
        @Override
        public StockItem save(StockItem stockItem) {
            savedCount++;
            items.put(stockItem.getId(), stockItem);
            return stockItem;
        }
        @Override
        public List<StockItem> search(
                org.ilestegor.home_medicine_tracker.application.port.in.stock.command.StockFilter f,
                Long userId) {
            return List.copyOf(items.values());
        }
        @Override
        public void deleteById(Long id, Long userId) {
            items.remove(id);
        }
        @Override
        public List<StockItem> findActiveExpiringUntil(LocalDate deadline, Long userId) {
            return List.of();
        }
        @Override
        public List<StockItem> findActiveExpiredBefore(LocalDate today, Long userId) {
            return List.of();
        }
        @Override
        public List<StockItem> findActiveBelow(BigDecimal threshold, Long userId) {
            return List.of();
        }
        @Override
        public java.util.Map<Long, BigDecimal> sumAvailableByMedicine(
                java.util.Collection<Long> medicineIds, LocalDate today, Long userId) {
            return java.util.Map.of();
        }
        @Override
        public int countActive(Long userId) {
            return items.size();
        }
    }
    private static class StubRegimens implements RegimenPersistencePort {
        private final java.util.Map<Long, Regimen> items = new java.util.HashMap<>();
        void put(Regimen regimen) {
            items.put(regimen.getId(), regimen);
        }

        @Override
        public Optional<Regimen> findById(Long id, Long userId) {
            return Optional.ofNullable(items.get(id));
        }
        @Override
        public List<Regimen> search(
                org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.RegimenFilter f,
                Long userId) {
            return List.copyOf(items.values());
        }
        @Override
        public Regimen save(Regimen regimen) {
            items.put(regimen.getId(), regimen);
            return regimen;
        }
        @Override
        public void deleteById(Long id, Long userId) {
            items.remove(id);
        }
        @Override
        public List<Regimen> findEffectiveOn(LocalDate date, Long userId) {
            return List.of();
        }
    }
    private static class StubIntakes implements IntakePersistencePort {
        private final List<IntakeRecord> saved = new ArrayList<>();
        @Override
        public Optional<IntakeRecord> findById(Long id, Long userId) {
            return Optional.empty();
        }
        @Override
        public org.ilestegor.home_medicine_tracker.application.common.PageResult<IntakeRecord> search(
                org.ilestegor.home_medicine_tracker.application.port.in.intake.command.IntakeFilter f,
                org.ilestegor.home_medicine_tracker.application.common.PageRequest p,
                Long userId) {
            return new org.ilestegor.home_medicine_tracker.application.common.PageResult<>(
                    saved, 0, saved.size(), saved.size());
        }
        @Override
        public IntakeRecord save(IntakeRecord record) {
            saved.add(record);
            return record;
        }
        @Override
        public void deleteById(Long id, Long userId) {
        }

        @Override
        public java.util.Map<Long, Long> countByRegimenBetween(
                java.util.Collection<Long> regimenIds, Instant from, Instant to, Long userId) {
            return java.util.Map.of();
        }
    }
}
