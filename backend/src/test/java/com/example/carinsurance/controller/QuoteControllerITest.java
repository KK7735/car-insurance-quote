package com.example.carinsurance.controller;

import com.example.carinsurance.domain.dto.QuoteRequest;
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
public class QuoteControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private QuoteRequest createValidRequest() {
        QuoteRequest request = new QuoteRequest();
        request.setDriverAge(35);
        request.setLicenseColor("GOLD");
        request.setUsageType("PRIVATE");
        request.setAnnualMileage(8000);
        request.setDriverRange("SELF");
        request.setHasCurrentInsurance(false);
        request.setMaker("TOYOTA");
        request.setCarName("PRIUS");
        request.setFirstRegistrationYearMonth("2024-01");
        request.setVehicleType("SEDAN");
        request.setVehicleInsurance(false);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("FIFTY_MILLION");
        request.setLawyerOption(false);
        request.setRoadService(false);
        return request;
    }

    @Test
    void testIT001_CreateQuote_Success() throws Exception {
        QuoteRequest request = createValidRequest();

        mockMvc.perform(post("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteNo", startsWith("EST")))
                .andExpect(jsonPath("$.annualPremium", notNullValue()))
                .andExpect(jsonPath("$.monthlyPremium", notNullValue()));
    }

    @Test
    void testIT002_CreateQuote_MissingRequiredFields_Returns400() throws Exception {
        QuoteRequest request = createValidRequest();
        request.setDriverAge(null); // Missing required field

        mockMvc.perform(post("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.details.driverAge", notNullValue()));
    }

    @Test
    void testIT003_GetQuote_ExistingQuoteNo_Returns200() throws Exception {
        QuoteRequest request = createValidRequest();

        MvcResult result = mockMvc.perform(post("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String quoteNo = objectMapper.readTree(responseBody).get("quoteNo").asText();

        mockMvc.perform(get("/api/quotes/{quoteNo}", quoteNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteNo", is(quoteNo)))
                .andExpect(jsonPath("$.annualPremium", notNullValue()))
                .andExpect(jsonPath("$.breakdowns", notNullValue()))
                .andExpect(jsonPath("$.breakdowns.length()", greaterThan(0)));
    }

    @Test
    void testIT004_GetQuote_NonExistingQuoteNo_Returns404() throws Exception {
        mockMvc.perform(get("/api/quotes/{quoteNo}", "EST999999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }
}
