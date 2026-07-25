package com.example.carinsurance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateMasterDto {
    private String category;
    private String itemCode;
    private String itemName;
    private BigDecimal rate;
    private Integer amount;
}
