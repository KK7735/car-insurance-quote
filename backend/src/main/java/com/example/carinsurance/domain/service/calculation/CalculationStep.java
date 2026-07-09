package com.example.carinsurance.domain.service.calculation;

import org.springframework.core.Ordered;

public interface CalculationStep extends Ordered {
    void calculate(QuoteContext context);
}
