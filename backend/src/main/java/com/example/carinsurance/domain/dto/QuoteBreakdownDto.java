package com.example.carinsurance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "見積計算内訳")
public class QuoteBreakdownDto {
    @Schema(description = "内訳名", example = "基本保険料")
    private String itemName;
    @Schema(description = "係数", example = "1.25")
    private BigDecimal rate;
    @Schema(description = "加算額", example = "5000")
    private Integer amount;
}
