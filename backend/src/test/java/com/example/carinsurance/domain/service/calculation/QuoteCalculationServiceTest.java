package com.example.carinsurance.domain.service.calculation;

import com.example.carinsurance.domain.entity.Quote;
import com.example.carinsurance.domain.entity.RateMaster;
import com.example.carinsurance.domain.repository.RateMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuoteCalculationServiceTest {

    @Mock
    private RateMasterRepository rateMasterRepository;
    private QuoteCalculationService service;

    @BeforeEach
    void setUp() {
        service = new QuoteCalculationService(rateMasterRepository, CalculationSteps.getAllSteps());
        when(rateMasterRepository.findByActiveTrue()).thenReturn(createStubRates());
    }

    private Quote createBaseQuote() {
        Quote quote = new Quote();
        quote.setDriverAge(35);
        quote.setLicenseColor("GOLD");
        quote.setUsageType("PRIVATE");
        quote.setAnnualMileage(8000);
        quote.setDriverRange("SELF");
        quote.setHasCurrentInsurance(true);
        quote.setGrade(20);
        quote.setAccidentTerm(0);
        quote.setVehicleType("SEDAN");
        quote.setVehicleInsurance(false);
        return quote;
    }

    private QuoteCalculationCommand createBaseCommand(Quote quote) {
        return QuoteCalculationCommand.builder()
                .quote(quote)
                .propertyDamageLimit("UNLIMITED")
                .personalInjuryAmount("FIFTY_MILLION")
                .lawyerOption(false)
                .roadService(false)
                .build();
    }

    @Test
    void testUT001_NormalQuote() {
        Quote result = service.calculate(createBaseCommand(createBaseQuote()));
        // Calc: 50000 * 1.0 * 0.9 * 1.0 * 1.0 * 0.9 * 0.8 * 1.0 = 32400
        // Additions: PropDmg +5000, PersInj50 +3000 = 8000
        // Total = 40400. Round -> 40400. Monthly = 3370.
        assertEquals(40400, result.getAnnualPremium());
        assertEquals(3370, result.getMonthlyPremium());
        // 内訳が保存されていること
        assertTrue(result.getBreakdowns().size() > 5);
    }

    @Test
    void testUT002_HighRiskQuote() {
        Quote quote = createBaseQuote();
        quote.setDriverAge(18); // 1.6
        quote.setLicenseColor("GREEN"); // 1.1
        quote.setUsageType("BUSINESS"); // 1.25
        quote.setAnnualMileage(15000); // 1.15
        quote.setDriverRange("ANYONE"); // 1.2
        quote.setGrade(3); // 1.3
        quote.setAccidentTerm(3); // 1.2
        quote.setVehicleType("SUV"); // 1.15
        quote.setVehicleInsurance(true); // +30000

        QuoteCalculationCommand cmd = QuoteCalculationCommand.builder()
                .quote(quote)
                .propertyDamageLimit("UNLIMITED") // +5000
                .personalInjuryAmount("UNLIMITED") // +7000
                .lawyerOption(true) // +2000
                .roadService(true) // +1500
                .build();

        Quote result = service.calculate(cmd);
        // Base calc = 50000 * 1.6 * 1.1 * 1.25 * 1.15 * 1.2 * 1.3 * 1.2 * 1.15 = 272329.2
        // Additions = 30000 + 5000 + 7000 + 2000 + 1500 = 45500
        // Total = 317829.2 -> 10円未満四捨五入 -> 317830
        // Monthly = 317830 / 12 = 26485.833 -> 10円未満四捨五入 -> 26490
        assertEquals(317830, result.getAnnualPremium());
        assertEquals(26490, result.getMonthlyPremium());
    }

    @Test
    void testUT003_AgeBoundaries() {
        // 25歳 (1.6)
        Quote quote25 = createBaseQuote(); quote25.setDriverAge(25);
        assertEquals("AGE_18_25", getAppliedCode(quote25, "AGE"));
        // 26歳 (1.25)
        Quote quote26 = createBaseQuote(); quote26.setDriverAge(26);
        assertEquals("AGE_26_34", getAppliedCode(quote26, "AGE"));
        // 34歳 (1.25)
        Quote quote34 = createBaseQuote(); quote34.setDriverAge(34);
        assertEquals("AGE_26_34", getAppliedCode(quote34, "AGE"));
        // 35歳 (1.0)
        Quote quote35 = createBaseQuote(); quote35.setDriverAge(35);
        assertEquals("AGE_35_59", getAppliedCode(quote35, "AGE"));
        // 59歳 (1.0)
        Quote quote59 = createBaseQuote(); quote59.setDriverAge(59);
        assertEquals("AGE_35_59", getAppliedCode(quote59, "AGE"));
        // 60歳 (1.2)
        Quote quote60 = createBaseQuote(); quote60.setDriverAge(60);
        assertEquals("AGE_60_OVER", getAppliedCode(quote60, "AGE"));
    }

    @Test
    void testUT004_MileageBoundaries() {
        Quote q1 = createBaseQuote(); q1.setAnnualMileage(5000);
        assertEquals("MILEAGE_0_5000", getAppliedCode(q1, "MILEAGE"));

        Quote q2 = createBaseQuote(); q2.setAnnualMileage(5001);
        assertEquals("MILEAGE_5001_10000", getAppliedCode(q2, "MILEAGE"));

        Quote q3 = createBaseQuote(); q3.setAnnualMileage(10000);
        assertEquals("MILEAGE_5001_10000", getAppliedCode(q3, "MILEAGE"));

        Quote q4 = createBaseQuote(); q4.setAnnualMileage(10001);
        assertEquals("MILEAGE_10001_OVER", getAppliedCode(q4, "MILEAGE"));
    }

    @Test
    void testUT005_GradeBoundaries() {
        Quote q5 = createBaseQuote(); q5.setGrade(5); assertEquals("GRADE_1_5", getAppliedCode(q5, "GRADE"));
        Quote q6 = createBaseQuote(); q6.setGrade(6); assertEquals("GRADE_6_10", getAppliedCode(q6, "GRADE"));
        Quote q10 = createBaseQuote(); q10.setGrade(10); assertEquals("GRADE_6_10", getAppliedCode(q10, "GRADE"));
        Quote q11 = createBaseQuote(); q11.setGrade(11); assertEquals("GRADE_11_15", getAppliedCode(q11, "GRADE"));
        Quote q15 = createBaseQuote(); q15.setGrade(15); assertEquals("GRADE_11_15", getAppliedCode(q15, "GRADE"));
        Quote q16 = createBaseQuote(); q16.setGrade(16); assertEquals("GRADE_16_20", getAppliedCode(q16, "GRADE"));
        Quote q20 = createBaseQuote(); q20.setGrade(20); assertEquals("GRADE_16_20", getAppliedCode(q20, "GRADE"));
    }

    @Test
    void testUT006_RoundingRule() {
        // 意図的に端数が出るように調整
        Quote quote = createBaseQuote();
        // Base = 50000 
        // 26歳(1.25), GOLD(0.9), PRIVATE(1.0), 8000km(1.0), SELF(0.9), SUV(1.15)
        // 50000 * 1.25 * 0.9 * 1.0 * 1.0 * 0.9 * 1.15 = 58218.75 
        // 四捨五入(10円) -> 58220.
        // Monthly = 58220 / 12 = 4851.666 -> 四捨五入(10円) -> 4850.
        quote.setDriverAge(26);
        quote.setVehicleType("SUV");
        quote.setGrade(null); quote.setHasCurrentInsurance(false);
        QuoteCalculationCommand cmd = QuoteCalculationCommand.builder()
                .quote(quote)
                .build(); // No additions
        Quote result = service.calculate(cmd);
        assertEquals(58220, result.getAnnualPremium());
        assertEquals(4850, result.getMonthlyPremium());
    }

    private String getAppliedCode(Quote quote, String category) {
        Quote result = service.calculate(createBaseCommand(quote));
        return result.getBreakdowns().stream()
                .filter(b -> rateMasterRepository.findByActiveTrue().stream()
                        .anyMatch(r -> r.getItemCode().equals(b.getItemCode()) && r.getCategory().equals(category)))
                .map(com.example.carinsurance.domain.entity.QuoteBreakdown::getItemCode)
                .findFirst().orElse(null);
    }

    private List<RateMaster> createStubRates() {
        return Arrays.asList(
                new RateMaster(1L, "AGE", "AGE_18_25", "18-25", new BigDecimal("1.600"), null, true),
                new RateMaster(2L, "AGE", "AGE_26_34", "26-34", new BigDecimal("1.250"), null, true),
                new RateMaster(3L, "AGE", "AGE_35_59", "35-59", new BigDecimal("1.000"), null, true),
                new RateMaster(4L, "AGE", "AGE_60_OVER", "60+", new BigDecimal("1.200"), null, true),
                new RateMaster(5L, "LICENSE", "GOLD", "GOLD", new BigDecimal("0.900"), null, true),
                new RateMaster(6L, "LICENSE", "BLUE", "BLUE", new BigDecimal("1.000"), null, true),
                new RateMaster(7L, "LICENSE", "GREEN", "GREEN", new BigDecimal("1.100"), null, true),
                new RateMaster(8L, "USAGE", "PRIVATE", "PRIVATE", new BigDecimal("1.000"), null, true),
                new RateMaster(9L, "USAGE", "COMMUTE", "COMMUTE", new BigDecimal("1.100"), null, true),
                new RateMaster(10L, "USAGE", "BUSINESS", "BUSINESS", new BigDecimal("1.250"), null, true),
                new RateMaster(11L, "MILEAGE", "MILEAGE_0_5000", "0-5000", new BigDecimal("0.950"), null, true),
                new RateMaster(12L, "MILEAGE", "MILEAGE_5001_10000", "5001-10000", new BigDecimal("1.000"), null, true),
                new RateMaster(13L, "MILEAGE", "MILEAGE_10001_OVER", "10001+", new BigDecimal("1.150"), null, true),
                new RateMaster(14L, "DRIVER_RANGE", "SELF", "SELF", new BigDecimal("0.900"), null, true),
                new RateMaster(15L, "DRIVER_RANGE", "COUPLE", "COUPLE", new BigDecimal("0.950"), null, true),
                new RateMaster(16L, "DRIVER_RANGE", "FAMILY", "FAMILY", new BigDecimal("1.050"), null, true),
                new RateMaster(17L, "DRIVER_RANGE", "ANYONE", "ANYONE", new BigDecimal("1.200"), null, true),
                new RateMaster(18L, "GRADE", "GRADE_1_5", "1-5", new BigDecimal("1.300"), null, true),
                new RateMaster(19L, "GRADE", "GRADE_6_10", "6-10", new BigDecimal("1.100"), null, true),
                new RateMaster(20L, "GRADE", "GRADE_11_15", "11-15", new BigDecimal("0.950"), null, true),
                new RateMaster(21L, "GRADE", "GRADE_16_20", "16-20", new BigDecimal("0.800"), null, true),
                new RateMaster(22L, "ACCIDENT_TERM", "HAS_TERM", "1+", new BigDecimal("1.200"), null, true),
                new RateMaster(23L, "VEHICLE_TYPE", "KEI", "KEI", new BigDecimal("0.900"), null, true),
                new RateMaster(24L, "VEHICLE_TYPE", "COMPACT", "COMPACT", new BigDecimal("0.950"), null, true),
                new RateMaster(25L, "VEHICLE_TYPE", "SEDAN", "SEDAN", new BigDecimal("1.000"), null, true),
                new RateMaster(26L, "VEHICLE_TYPE", "MINIVAN", "MINIVAN", new BigDecimal("1.100"), null, true),
                new RateMaster(27L, "VEHICLE_TYPE", "SUV", "SUV", new BigDecimal("1.150"), null, true),
                new RateMaster(28L, "VEHICLE_INSURANCE", "WITH_INSURANCE", "VehIns", null, 30000, true),
                new RateMaster(29L, "PROPERTY_DAMAGE", "UNLIMITED", "PropDmg", null, 5000, true),
                new RateMaster(30L, "PERSONAL_INJURY", "FIFTY_MILLION", "PersInj50", null, 3000, true),
                new RateMaster(31L, "PERSONAL_INJURY", "UNLIMITED", "PersInjUnl", null, 7000, true),
                new RateMaster(32L, "LAWYER_OPTION", "WITH_OPTION", "Lawyer", null, 2000, true),
                new RateMaster(33L, "ROAD_SERVICE", "WITH_SERVICE", "Road", null, 1500, true)
        );
    }
}
