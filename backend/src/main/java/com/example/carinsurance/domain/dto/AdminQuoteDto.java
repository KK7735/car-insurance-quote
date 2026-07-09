package com.example.carinsurance.domain.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminQuoteDto {
    private String quoteNo;
    private Integer driverAge;
    private String licenseColor;
    private String usageType;
    private Integer annualMileage;
    private String driverRange;
    private Boolean hasCurrentInsurance;
    private Integer grade;
    private Integer accidentTerm;
    private String maker;
    private String carName;
    private String firstRegistrationYm;
    private String vehicleType;
    private Boolean vehicleInsurance;
    private Integer annualPremium;
    private Integer monthlyPremium;
    private LocalDateTime createdAt;
}
