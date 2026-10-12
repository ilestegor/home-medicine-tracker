package org.ilestegor.home_medicine_tracker.api;

import com.jayway.jsonpath.JsonPath;
import org.ilestegor.home_medicine_tracker.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unchecked")
class FilterAndPagingIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Фильтры упаковок комбинируются: medicineId + status")
    void stockFiltersCombine() throws Exception {
        long medicine = createMedicine(uniqueName(), "200 мг");
        long active = createStock(medicine, "10", LocalDate.now().plusYears(1));
        long discarded = createStock(medicine, "5", LocalDate.now().plusYears(1));

        mvc.perform(post("/api/v1/stock-items/{id}/discard", discarded))
                .andExpect(status().isOk());

        String all = bodyOf(mvc.perform(get("/api/v1/stock-items")
                        .param("medicineId", String.valueOf(medicine)))
                .andExpect(status().isOk()));
        assertThat((List<Integer>) JsonPath.read(all, "$[*].id")).hasSize(2);

        String onlyActive = bodyOf(mvc.perform(get("/api/v1/stock-items")
                        .param("medicineId", String.valueOf(medicine))
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk()));
        assertThat((List<Integer>) JsonPath.read(onlyActive, "$[*].id"))
                .as("status должен применяться вместе с medicineId")
                .containsExactly((int) active);
    }

    @Test
    @DisplayName("Фильтры схем комбинируются: medicineId + active")
    void regimenFiltersCombine() throws Exception {
        long medicine = createMedicine(uniqueName(), "50 мг");
        long activeRegimen = createRegimen(medicine, "1", 2);
        long inactiveRegimen = createRegimen(medicine, "2", 1);

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/v1/regimens/{id}", inactiveRegimen)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"medicineId": %d, "doseAmount": 2, "timesPerDay": 1,
                                 "startsOn": "%s", "active": false}
                                """.formatted(medicine, LocalDate.now().minusDays(1))))
                .andExpect(status().isOk());

        String body = bodyOf(mvc.perform(get("/api/v1/regimens")
                        .param("medicineId", String.valueOf(medicine))
                        .param("active", "true"))
                .andExpect(status().isOk()));

        assertThat((List<Integer>) JsonPath.read(body, "$[*].id"))
                .containsExactly((int) activeRegimen);
    }

    @Test
    @DisplayName("Журнал приёмов отдаётся страницей")
    void intakeJournalIsPaged() throws Exception {
        long medicine = createMedicine(uniqueName(), "5 мг");
        long stock = createStock(medicine, "10", LocalDate.now().plusYears(1));
        takeIntake(stock, null, "1");
        takeIntake(stock, null, "1");
        takeIntake(stock, null, "1");

        mvc.perform(get("/api/v1/intakes")
                        .param("stockItemId", String.valueOf(stock))
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(true));

        mvc.perform(get("/api/v1/intakes")
                        .param("stockItemId", String.valueOf(stock))
                        .param("size", "2").param("page", "1"))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("Разовый приём без курса попадает в выборку")
    void intakeWithoutRegimenIsNotLost() throws Exception {
        long medicine = createMedicine(uniqueName(), "1 мг");
        long stock = createStock(medicine, "10", LocalDate.now().plusYears(1));
        takeIntake(stock, null, "1");

        String body = bodyOf(mvc.perform(get("/api/v1/intakes")
                        .param("stockItemId", String.valueOf(stock)))
                .andExpect(status().isOk()));

        List<Map<String, Object>> content = JsonPath.read(body, "$.content");
        assertThat(content).hasSize(1);
        assertThat(content.getFirst().get("byRegimen")).isEqualTo(false);
        assertThat(content.getFirst().get("regimenId")).isNull();
    }

    @Test
    @DisplayName("Граница по дате работает и по одному концу")
    void dateBoundWorksWithSingleEnd() throws Exception {
        long medicine = createMedicine(uniqueName(), "2 мг");
        long stock = createStock(medicine, "10", LocalDate.now().plusYears(1));
        takeIntake(stock, null, "1");

        Instant hourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant hourAhead = Instant.now().plus(1, ChronoUnit.HOURS);

        mvc.perform(get("/api/v1/intakes")
                        .param("stockItemId", String.valueOf(stock))
                        .param("from", hourAgo.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));

        mvc.perform(get("/api/v1/intakes")
                        .param("stockItemId", String.valueOf(stock))
                        .param("from", hourAhead.toString()))
                .andExpect(jsonPath("$.totalElements").value(0));

        mvc.perform(get("/api/v1/intakes")
                        .param("stockItemId", String.valueOf(stock))
                        .param("to", hourAgo.toString()))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
