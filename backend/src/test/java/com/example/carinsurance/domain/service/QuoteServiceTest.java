package com.example.carinsurance.domain.service;

import com.example.carinsurance.domain.dto.QuoteRequest;
import com.example.carinsurance.domain.dto.QuoteResponse;
import com.example.carinsurance.domain.entity.Quote;
import com.example.carinsurance.domain.entity.QuoteBreakdown;
import com.example.carinsurance.domain.repository.QuoteRepository;
import com.example.carinsurance.domain.service.calculation.QuoteCalculationCommand;
import com.example.carinsurance.domain.service.calculation.QuoteCalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceTest {

    @Mock
    private QuoteCalculationService calculationService;

    @Mock
    private QuoteRepository quoteRepository;

    @InjectMocks
    private QuoteService quoteService;

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
    void testUT009_QuoteNoGeneration_UniquePerDay() {
        QuoteRequest request = createValidRequest();
        Quote calculatedQuote = new Quote();
        calculatedQuote.setAnnualPremium(50000);
        calculatedQuote.setMonthlyPremium(4170);

        when(calculationService.calculate(any(QuoteCalculationCommand.class))).thenReturn(calculatedQuote);
        when(quoteRepository.count()).thenReturn(0L, 1L);
        when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuoteResponse response1 = quoteService.createQuote(request);
        QuoteResponse response2 = quoteService.createQuote(request);

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertEquals("EST" + dateStr + "0001", response1.getQuoteNo());
        assertEquals("EST" + dateStr + "0002", response2.getQuoteNo());
    }

    @Test
    void testUT010_QuoteBreakdownSaved() {
        QuoteRequest request = createValidRequest();
        Quote calculatedQuote = new Quote();
        calculatedQuote.setAnnualPremium(50000);
        calculatedQuote.setMonthlyPremium(4170);

        QuoteBreakdown breakdown = new QuoteBreakdown();
        breakdown.setItemCode("AGE_35_59");
        breakdown.setItemName("35-59");
        breakdown.setRate(new BigDecimal("1.000"));
        calculatedQuote.addBreakdown(breakdown);

        when(calculationService.calculate(any(QuoteCalculationCommand.class))).thenReturn(calculatedQuote);
        when(quoteRepository.count()).thenReturn(0L);
        when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        quoteService.createQuote(request);

        ArgumentCaptor<Quote> quoteCaptor = ArgumentCaptor.forClass(Quote.class);
        verify(quoteRepository).save(quoteCaptor.capture());
        
        Quote savedQuote = quoteCaptor.getValue();
        assertFalse(savedQuote.getBreakdowns().isEmpty(), "Breakdowns should be saved in the database");
        assertEquals("AGE_35_59", savedQuote.getBreakdowns().get(0).getItemCode());
    }
}
