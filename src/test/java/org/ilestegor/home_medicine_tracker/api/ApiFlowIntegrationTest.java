package org.ilestegor.home_medicine_tracker.api;

import org.ilestegor.home_medicine_tracker.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Приём списывает остаток, удаление приёма возвращает")
    void intakeWithdrawsAndDeletionRestores() throws Exception {
        long medicine = createMedicine(uniqueName(), "200 мг");
        long stock = createStock(medicine, "10", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "1", 3);

        long intake = idOf(mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockItemId": %d, "regimenId": %d, "amount": 2}
                                """.formatted(stock, regimen)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.byRegimen").value(true))
                .andExpect(jsonPath("$.stockItemQuantityAfter").value(8.00)));

        mvc.perform(get("/api/v1/stock-items/{id}", stock))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(8.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.medicine.form").value("TABLET"));

        mvc.perform(delete("/api/v1/intakes/{id}", intake))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/stock-items/{id}", stock))
                .andExpect(jsonPath("$.quantity").value(10.00));
    }

    @Test
    @DisplayName("Списание до нуля закрывает упаковку")
    void withdrawalToZeroMarksFinished() throws Exception {
        long medicine = createMedicine(uniqueName(), "500 мг");
        long stock = createStock(medicine, "2", LocalDate.now().plusYears(1));

        mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockItemId": %d, "amount": 2}
                                """.formatted(stock)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/stock-items/{id}", stock))
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.quantity").value(0.00));
    }

    @Test
    @DisplayName("Списать больше остатка — 409, остаток не меняется")
    void withdrawalAboveQuantityReturnsConflict() throws Exception {
        long medicine = createMedicine(uniqueName(), null);
        long stock = createStock(medicine, "3", LocalDate.now().plusYears(1));

        mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockItemId": %d, "amount": 4}
                                """.formatted(stock)))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/v1/stock-items/{id}", stock))
                .andExpect(jsonPath("$.quantity").value(3.00));
    }

    @Test
    @DisplayName("Ромб-инвариант: схема и упаковка от разных препаратов — 409")
    void regimenForDifferentMedicineReturnsConflict() throws Exception {
        long ibuprofen = createMedicine(uniqueName(), "200 мг");
        long amoxicillin = createMedicine(uniqueName(), "500 мг");

        long stock = createStock(ibuprofen, "10", LocalDate.now().plusYears(1));
        long foreignRegimen = createRegimen(amoxicillin, "1", 2);

        mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockItemId": %d, "regimenId": %d, "amount": 1}
                                """.formatted(stock, foreignRegimen)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Единица берётся у препарата — разойтись упаковке и схеме негде")
    void unitBelongsToMedicine() throws Exception {
        long medicine = createMedicine(uniqueName(), "100 мг/5 мл", "мл");
        long stock = createStock(medicine, "100", LocalDate.now().plusYears(1));
        long regimen = createRegimen(medicine, "5", 3);

        mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockItemId": %d, "regimenId": %d, "amount": 5}
                                """.formatted(stock, regimen)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicine.unit").value("мл"))
                .andExpect(jsonPath("$.stockItemQuantityAfter").value(95.00));
    }

    @Test
    @DisplayName("Приём из просроченной упаковки — 409")
    void intakeFromExpiredPackReturnsConflict() throws Exception {
        long medicine = createMedicine(uniqueName(), null);
        long stock = createStock(medicine, "10", LocalDate.now().minusDays(1));

        mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockItemId": %d, "amount": 1}
                                """.formatted(stock)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Удалить препарат с упаковками нельзя — 409 от ON DELETE RESTRICT")
    void deletingMedicineWithStockReturnsConflict() throws Exception {
        long medicine = createMedicine(uniqueName(), null);
        createStock(medicine, "10", LocalDate.now().plusYears(1));

        mvc.perform(delete("/api/v1/medicines/{id}", medicine))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Дубль препарата отбивается ограничением БД — 409")
    void duplicateMedicineReturnsConflict() throws Exception {
        String name = uniqueName();
        createMedicine(name, "200 мг");

        mvc.perform(post("/api/v1/medicines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "dosage": "200 мг", "unit": "шт"}
                                """.formatted(name)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Пустое название — 400 с перечнем полей")
    void blankNameReturnsBadRequestWithFields() throws Exception {
        String response = mvc.perform(post("/api/v1/medicines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.name").exists())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(response).contains("название обязательно");
    }

    @Test
    @DisplayName("Несуществующий id — 404")
    void unknownIdReturnsNotFound() throws Exception {
        mvc.perform(get("/api/v1/stock-items/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Выборки: истекает, заканчивается, действует сегодня")
    void expiringLowStockAndEffectiveTodayViews() throws Exception {
        long medicine = createMedicine(uniqueName(), "10 мг");
        long soon = createStock(medicine, "1", LocalDate.now().plusDays(10));
        long regimen = createRegimen(medicine, "1", 1);

        mvc.perform(get("/api/v1/stock-items/expiring").param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %d)]".formatted(soon)).exists());

        mvc.perform(get("/api/v1/stock-items/low-stock").param("below", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %d)]".formatted(soon)).exists());

        mvc.perform(get("/api/v1/regimens/effective-today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %d)]".formatted(regimen)).exists());
    }

    @Test
    @DisplayName("Выброшенная упаковка не принимает списание")
    void discardedPackRejectsIntake() throws Exception {
        long medicine = createMedicine(uniqueName(), null);
        long stock = createStock(medicine, "10", LocalDate.now().plusYears(1));

        mvc.perform(post("/api/v1/stock-items/{id}/discard", stock))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISCARDED"));

        mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockItemId": %d, "amount": 1}
                                """.formatted(stock)))
                .andExpect(status().isConflict());
    }
}
