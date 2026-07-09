package com.example.carinsurance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

// 見積もりリクエストの DTO。Bean Validation アノテーションを利用して、すべての入力フィールドに対して厳格な境界およびホワイトリストのバリデーションを行う。これは悪意のあるデータ構築を防ぐための第一の防衛線である。
@Data
@Schema(description = "見積作成リクエスト")
public class QuoteRequest {
    @NotNull
    @Min(18) @Max(100)
    @Schema(description = "運転者年齢", example = "35")
    private Integer driverAge;

    @NotBlank
    @Pattern(regexp = "^(GOLD|BLUE|GREEN)$")
    @Schema(description = "免許証色", example = "GOLD")
    private String licenseColor;

    @NotBlank
    @Pattern(regexp = "^(PRIVATE|COMMUTE|BUSINESS)$")
    @Schema(description = "使用目的", example = "PRIVATE")
    private String usageType;

    @NotNull
    @Min(0) @Max(30000)
    @Schema(description = "年間走行距離", example = "8000")
    private Integer annualMileage;

    @NotBlank
    @Pattern(regexp = "^(SELF|COUPLE|FAMILY|ANYONE)$")
    @Schema(description = "運転者範囲", example = "SELF")
    private String driverRange;

    @NotNull
    @Schema(description = "現在加入有無", example = "true")
    private Boolean hasCurrentInsurance;

    @Schema(description = "等級 (加入ありの場合必須 1-20)", example = "20")
    private Integer grade;

    @Schema(description = "事故有係数期間 (加入ありの場合必須 0-6)", example = "0")
    private Integer accidentTerm;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "メーカー", example = "TOYOTA")
    private String maker;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "車名", example = "PRIUS")
    private String carName;

    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}$")
    @Schema(description = "初度登録年月 (YYYY-MM)", example = "2024-01")
    private String firstRegistrationYearMonth;

    @NotBlank
    @Pattern(regexp = "^(COMPACT|SEDAN|MINIVAN|SUV|KEI)$")
    @Schema(description = "車両タイプ", example = "SEDAN")
    private String vehicleType;

    @NotNull
    @Schema(description = "車両保険有無", example = "false")
    private Boolean vehicleInsurance;

    @NotBlank
    @Pattern(regexp = "^(UNLIMITED|THIRTY_MILLION)$")
    @Schema(description = "対物補償", example = "UNLIMITED")
    private String propertyDamageLimit;

    @NotBlank
    @Pattern(regexp = "^(THIRTY_MILLION|FIFTY_MILLION|UNLIMITED)$")
    @Schema(description = "人身傷害", example = "FIFTY_MILLION")
    private String personalInjuryAmount;

    @NotNull
    @Schema(description = "弁護士特約有無", example = "false")
    private Boolean lawyerOption;

    @NotNull
    @Schema(description = "ロードサービス有無", example = "false")
    private Boolean roadService;

    // SC-003 クロスフィールドバリデーション
    @AssertTrue(message = "現在加入ありの場合、等級(1-20)と事故有係数期間(0-6)は必須です")
    @Schema(hidden = true)
    public boolean isInsuranceDetailsValid() {
        if (Boolean.TRUE.equals(hasCurrentInsurance)) {
            if (grade == null || grade < 1 || grade > 20) return false;
            if (accidentTerm == null || accidentTerm < 0 || accidentTerm > 6) return false;
        }
        return true;
    }

    // 初度登録年月の未来日付チェック
    @AssertTrue(message = "初度登録年月は未来の年月を指定できません")
    @Schema(hidden = true)
    public boolean isFirstRegistrationYearMonthValid() {
        if (firstRegistrationYearMonth == null) return true;
        try {
            java.time.YearMonth inputYm = java.time.YearMonth.parse(firstRegistrationYearMonth);
            java.time.YearMonth currentYm = java.time.YearMonth.now();
            return !inputYm.isAfter(currentYm);
        } catch (Exception e) {
            return false;
        }
    }
}
