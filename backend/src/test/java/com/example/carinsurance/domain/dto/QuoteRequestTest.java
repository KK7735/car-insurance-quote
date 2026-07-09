package com.example.carinsurance.domain.dto;

import com.example.carinsurance.domain.controller.QuoteController;
import com.example.carinsurance.domain.exception.GlobalExceptionHandler;
import com.example.carinsurance.domain.service.QuoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class QuoteRequestTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private QuoteService quoteService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new QuoteController(quoteService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

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
    void testUT007_DriverAgeValidation_ErrorWhenAgeIs17() throws Exception {
        QuoteRequest request = createValidRequest();
        request.setDriverAge(17);

        mockMvc.perform(post("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.details.driverAge", notNullValue()));
    }

    @Test
    void testUT008_InsuranceDetailsValidation_ErrorWhenGradeIsMissing() throws Exception {
        QuoteRequest request = createValidRequest();
        request.setHasCurrentInsurance(true);
        request.setGrade(null);
        request.setAccidentTerm(0);

        mockMvc.perform(post("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.details.insuranceDetailsValid", notNullValue()));
    }

    @Test
    void testUT011_FirstRegistrationYearMonthValidation_ErrorWhenFuture() throws Exception {
        QuoteRequest request = createValidRequest();
        java.time.YearMonth futureYm = java.time.YearMonth.now().plusMonths(1);
        request.setFirstRegistrationYearMonth(futureYm.toString()); // YYYY-MM

        mockMvc.perform(post("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.details.firstRegistrationYearMonthValid", notNullValue()));
    }
}
