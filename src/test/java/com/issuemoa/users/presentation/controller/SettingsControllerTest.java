package com.issuemoa.users.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuemoa.users.application.SettingsService;
import com.issuemoa.users.presentation.dto.settings.SettingsRequest;
import com.issuemoa.users.presentation.dto.settings.SettingsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(SettingsController.class)
class SettingsControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private SettingsService settingsService;

    @BeforeEach
    void setUp(WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }

    @Test
    void save() throws Exception {
        // given
        SettingsRequest request = new SettingsRequest(0L, "D", "W", "F", "EN");
        SettingsResponse response = new SettingsResponse(1L, 0L, "D", "W", "F", "EN");

        when(settingsService.save(any(SettingsRequest.class))).thenReturn(response);

        // when / then
        mockMvc.perform(post("/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))) // Serialize request to JSON
                .andExpect(status().isOk()) // HTTP 상태 확인
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 응답 ContentType 확인
                .andExpect(jsonPath("$.id").value(1L)) // 응답 필드 확인
                .andExpect(jsonPath("$.theme").value("D"))
                .andExpect(jsonPath("$.voice").value("W"))
                .andExpect(jsonPath("$.speed").value("F"))
                .andExpect(jsonPath("$.language").value("EN"));

        verify(settingsService).save(any(SettingsRequest.class));
    }

    @Test
    void findByUserId() throws Exception {
        // given
        Long userId = 1L;
        SettingsResponse response = new SettingsResponse(1L, 0L, "D", "W", "F", "EN");

        when(settingsService.findByUserId(userId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/settings/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("D"))
                .andExpect(jsonPath("$.language").value("EN"))
                .andExpect(jsonPath("$.speed").value("F"))
                .andExpect(jsonPath("$.voice").value("W"));
    }

    @Test
    void updateSettings() throws Exception {
        // given
        SettingsRequest request = new SettingsRequest(0L, "D", "W", "F", "EN");
        SettingsResponse response = new SettingsResponse(1L, 0L, "D", "W", "F", "EN");

        when(settingsService.updateSettings(request)).thenReturn(response);

        // when & then
        mockMvc.perform(put("/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))) // Serialize request to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("D"))
                .andExpect(jsonPath("$.language").value("EN"))
                .andExpect(jsonPath("$.speed").value("F"))
                .andExpect(jsonPath("$.voice").value("W"));
    }
}