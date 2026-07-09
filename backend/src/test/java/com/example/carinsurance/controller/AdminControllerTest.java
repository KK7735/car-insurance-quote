package com.example.carinsurance.controller;

import com.example.carinsurance.domain.dto.AdminLoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getAuthToken() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("password");

        MvcResult result = mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void testIT005_AdminLogin_Success() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("password");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void testIT006_GetQuotes_WithoutAuth_Returns401() throws Exception {
        mockMvc.perform(get("/api/admin/quotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testIT007_GetQuotes_WithAuth_Returns200() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/admin/quotes")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue())); // Spring Data Page wrapper
    }

    @Test
    void testIT008_DownloadCsv_WithAuth_Success() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/admin/quotes/csv")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=\"quotes.csv\"")))
                .andExpect(content().string(containsString("見積番号")));
    }
}
