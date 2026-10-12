package org.ilestegor.home_medicine_tracker.api;

import org.ilestegor.home_medicine_tracker.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:5173")
class CorsIntegrationTest extends AbstractIntegrationTest {
    private static final String VITE = "http://localhost:5173";
    @Test
    @DisplayName("Preflight с разрешённого origin пропускается")
    void preflightFromAllowedOriginSucceeds() throws Exception {
        mvc.perform(options("/api/v1/medicines")
                        .header("Origin", VITE)
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", VITE))
                .andExpect(header().stringValues("Access-Control-Allow-Methods",
                        "GET,POST,PUT,DELETE,OPTIONS"));
    }
    @Test
    @DisplayName("Простой запрос получает заголовок Allow-Origin")
    void simpleRequestGetsAllowOriginHeader() throws Exception {
        mvc.perform(get("/api/v1/medicines").header("Origin", VITE))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", VITE));
    }
    @Test
    @DisplayName("Чужой origin не получает разрешения")
    void foreignOriginIsRejected() throws Exception {
        mvc.perform(options("/api/v1/medicines")
                        .header("Origin", "http://evil.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
