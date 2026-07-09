package com.example.carinsurance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "見積結果レスポンス")
public class QuoteResponse {
    @Schema(description = "見積番号")
    private String quoteNo;
    
    @Schema(description = "年間保険料")
    private Integer annualPremium;
    
    @Schema(description = "月額保険料")
    private Integer monthlyPremium;
    
    @Schema(description = "計算内訳")
    private List<QuoteBreakdownDto> breakdowns;
    
    @Schema(description = "作成日時")
    private LocalDateTime createdAt;
}
