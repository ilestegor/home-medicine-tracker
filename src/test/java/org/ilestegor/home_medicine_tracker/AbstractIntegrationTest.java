package org.ilestegor.home_medicine_tracker;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.jwt.secret=test-secret-not-for-production-32b+")
public abstract class AbstractIntegrationTest {
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");
    static {
        POSTGRES.start();
    }
    @Autowired
    private WebApplicationContext context;

    protected MockMvc mvc;

    @BeforeEach
    void registerAndAuthenticate() throws Exception {
        MockMvc anonymous = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        String body = """
                {"email": "%s", "password": "very-secret-1", "displayName": "Тест"}
                """.formatted("u-" + UUID.randomUUID() + "@example.test");

        String token = JsonPath.read(bodyOf(anonymous.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())), "$.token");

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(get("/").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .build();
    }
    protected static String uniqueName() {
        return "Тест-" + UUID.randomUUID().toString().substring(0, 8);
    }
    protected long createMedicine(String name, String dosage) throws Exception {
        return createMedicine(name, dosage, "шт");
    }
    protected long createMedicine(String name, String dosage, String unit) throws Exception {
        String body = """
                {"name": "%s", "inn": "ибупрофен", "form": "TABLET",
                 "dosage": "%s", "unit": "%s"}
                """.formatted(name, dosage, unit);
        return idOf(mvc.perform(post("/api/v1/medicines")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()));
    }
    protected long createStock(long medicineId, String qty, LocalDate expires) throws Exception {
        String body = """
                {"medicineId": %d, "quantity": %s, "expiresAt": "%s"}
                """.formatted(medicineId, qty, expires);
        return idOf(mvc.perform(post("/api/v1/stock-items")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()));
    }
    protected long createRegimen(long medicineId, String dose, int timesPerDay) throws Exception {
        return createRegimen(medicineId, dose, timesPerDay, LocalDate.now().plusDays(9));
    }
    protected long createRegimen(long medicineId, String dose, int timesPerDay, LocalDate endsOn)
            throws Exception {
        String body = """
                {"medicineId": %d, "doseAmount": %s, "timesPerDay": %d,
                 "startsOn": "%s", "endsOn": %s}
                """.formatted(medicineId, dose, timesPerDay, LocalDate.now().minusDays(1),
                endsOn == null ? "null" : "\"" + endsOn + "\"");
        return idOf(mvc.perform(post("/api/v1/regimens")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()));
    }

    protected void takeIntake(long stockItemId, Long regimenId, String amount) throws Exception {
        String body = """
                {"stockItemId": %d, "regimenId": %s, "amount": %s}
                """.formatted(stockItemId, regimenId == null ? "null" : regimenId, amount);
        mvc.perform(post("/api/v1/intakes")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }
    protected long idOf(ResultActions actions) throws Exception {
        return ((Number) JsonPath.read(bodyOf(actions), "$.id")).longValue();
    }

    protected String bodyOf(ResultActions actions) throws Exception {
        return actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
}
