package com.example.carinsurance.domain.service.calculation;

import com.example.carinsurance.domain.entity.Quote;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuoteCalculationCommand {
    private Quote quote;
    private String propertyDamageLimit;
    private String personalInjuryAmount;
    private Boolean lawyerOption;
    private Boolean roadService;
}
