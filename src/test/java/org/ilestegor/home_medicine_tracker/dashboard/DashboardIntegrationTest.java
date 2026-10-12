package org.ilestegor.home_medicine_tracker.dashboard;

import com.jayway.jsonpath.JsonPath;
import org.ilestegor.home_medicine_tracker.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardIntegrationTest extends AbstractIntegrationTest {
    private Map<String, Object> todayRowFor(long regimenId) throws Exception {
        String body = bodyOf(mvc.perform(get("/api/v1/dashboard/today"))
                .andExpect(status().isOk()));
        List<Map<String, Object>> rows =
                JsonPath.read(body, "$[?(@.regimenId == %d)]".formatted(regimenId));
        assertThat(rows).as("схема %d должна быть в выборке на сегодня", regimenId).hasSize(1);
        return rows.getFirst();
    }
    private static BigDecimal decimal(Object value) {
        return new BigDecimal(String.valueOf(value));
    }

    @Test
    @DisplayName("План на сегодня и учёт уже принятого")
    void showsPlanAndAlreadyTakenToday() throws Exception {
        long medicine = createMedicine(uniqueName(), "200 мг");
        long stock = createStock(medicine, "20", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 3);
        Map<String, Object> before = todayRowFor(regimen);
        assertThat(before.get("timesPerDay")).isEqualTo(3);
        assertThat(before.get("takenToday")).isEqualTo(0);
        assertThat(before.get("remainingToday")).isEqualTo(3);
        assertThat(before.get("enoughForToday")).isEqualTo(true);
        takeIntake(stock, regimen, "1");
        Map<String, Object> after = todayRowFor(regimen);
        assertThat(after.get("takenToday")).isEqualTo(1);
        assertThat(after.get("remainingToday")).isEqualTo(2);
        assertThat(decimal(after.get("availableQuantity"))).isEqualByComparingTo("19");
    }
    @Test
    @DisplayName("Прогноз запаса: полные сутки, не дробные")
    void forecastCountsWholeDaysOnly() throws Exception {
        long medicine = createMedicine(uniqueName(), "500 мг");
        createStock(medicine, "7", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 3);
        Map<String, Object> row = todayRowFor(regimen);
        assertThat(decimal(row.get("dailyDose"))).isEqualByComparingTo("3");
        assertThat(row.get("daysOfStockLeft")).isEqualTo(2);
        assertThat(row.get("stockRunsOutOn")).isEqualTo(LocalDate.now().plusDays(2).toString());
    }
    @Test
    @DisplayName("Запаса не хватит до конца курса")
    void stockRunsOutBeforeCourseEnds() throws Exception {
        long medicine = createMedicine(uniqueName(), "10 мг");
        createStock(medicine, "4", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 2);
        Map<String, Object> row = todayRowFor(regimen);
        assertThat(row.get("daysOfStockLeft")).isEqualTo(2);
        assertThat(row.get("courseEndsOn")).isEqualTo(LocalDate.now().plusDays(9).toString());
        assertThat(row.get("stockLastsUntilCourseEnd")).isEqualTo(false);
    }
    @Test
    @DisplayName("Запаса хватит до конца курса")
    void stockLastsUntilCourseEnd() throws Exception {
        long medicine = createMedicine(uniqueName(), "20 мг");
        createStock(medicine, "60", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 2);
        assertThat(todayRowFor(regimen).get("stockLastsUntilCourseEnd")).isEqualTo(true);
    }
    @Test
    @DisplayName("Для бессрочного курса вопрос «хватит до конца» не задаётся")
    void openEndedCourseHasNoLastsUntilEndFlag() throws Exception {
        long medicine = createMedicine(uniqueName(), "5 мг");
        createStock(medicine, "30", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 1, null);
        Map<String, Object> row = todayRowFor(regimen);
        assertThat(row.get("courseEndsOn")).isNull();
        assertThat(row.get("stockLastsUntilCourseEnd"))
                .as("бессрочному курсу конца нет — сравнивать не с чем")
                .isNull();
    }
    @Test
    @DisplayName("Запаса не хватает на оставшиеся приёмы сегодня")
    void notEnoughForRemainingDosesToday() throws Exception {
        long medicine = createMedicine(uniqueName(), "1 мг");
        createStock(medicine, "1", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 3);
        Map<String, Object> row = todayRowFor(regimen);
        assertThat(row.get("remainingToday")).isEqualTo(3);
        assertThat(decimal(row.get("availableQuantity"))).isEqualByComparingTo("1");
        assertThat(row.get("enoughForToday"))
                .as("на три приёма нужно 3, а есть 1")
                .isEqualTo(false);
    }
    @Test
    @DisplayName("Просроченные упаковки в запас не идут")
    void expiredPacksAreNotCountedAsStock() throws Exception {
        long medicine = createMedicine(uniqueName(), "15 мг");
        createStock(medicine, "50", LocalDate.now().minusDays(1));
        long regimen = createRegimen(medicine, "1", 1);
        Map<String, Object> row = todayRowFor(regimen);
        assertThat(decimal(row.get("availableQuantity")))
                .as("50 просроченных таблеток запасом не являются")
                .isEqualByComparingTo("0");
        assertThat(row.get("daysOfStockLeft")).isEqualTo(0);
        assertThat(row.get("enoughForToday")).isEqualTo(false);
    }
    @Test
    @DisplayName("Выброшенные упаковки в запас не идут")
    void discardedPacksAreNotCountedAsStock() throws Exception {
        long medicine = createMedicine(uniqueName(), "25 мг");
        long stock = createStock(medicine, "40", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 1);
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/v1/stock-items/{id}/discard", stock))
                .andExpect(status().isOk());

        assertThat(decimal(todayRowFor(regimen).get("availableQuantity")))
                .isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Сводка отвечает и считает согласованно")
    void summaryIsConsistent() throws Exception {
        long medicine = createMedicine(uniqueName(), "30 мг");
        long stock = createStock(medicine, "10", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 2);
        takeIntake(stock, regimen, "1");
        String body = bodyOf(mvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isOk()));
        assertThat((String) JsonPath.read(body, "$.date"))
                .isEqualTo(LocalDate.now().toString());
        long planned = ((Number) JsonPath.read(body, "$.dosesPlannedToday")).longValue();
        long taken = ((Number) JsonPath.read(body, "$.dosesTakenToday")).longValue();
        long remaining = ((Number) JsonPath.read(body, "$.dosesRemainingToday")).longValue();
        int regimens = ((Number) JsonPath.read(body, "$.effectiveRegimens")).intValue();
        assertThat(regimens).isPositive();
        assertThat(planned).isGreaterThanOrEqualTo(2);
        assertThat(taken).isGreaterThanOrEqualTo(1);
        assertThat(remaining)
                .as("остаток приёмов не может превышать план")
                .isLessThanOrEqualTo(planned);
        assertThat((Number) JsonPath.read(body, "$.medicinesCount")).isNotNull();
    }
}
