package com.example.carinsurance.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 見積もりメインテーブルのエンティティ。ユーザーのコアな選択肢と計算結果を保存し、QuoteBreakdown と一対多の関係を構成する。
@Entity
@Table(name = "quotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quote_no", nullable = false, unique = true, length = 20)
    private String quoteNo;

    @Column(name = "driver_age", nullable = false)
    private Integer driverAge;

    @Column(name = "license_color", nullable = false, length = 20)
    private String licenseColor;

    @Column(name = "usage_type", nullable = false, length = 20)
    private String usageType;

    @Column(name = "annual_mileage", nullable = false)
    private Integer annualMileage;

    @Column(name = "driver_range", nullable = false, length = 20)
    private String driverRange;

    @Column(name = "has_current_insurance", nullable = false)
    private Boolean hasCurrentInsurance;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "accident_term")
    private Integer accidentTerm;

    @Column(name = "maker", nullable = false, length = 50)
    private String maker;

    @Column(name = "car_name", nullable = false, length = 50)
    private String carName;

    @Column(name = "first_registration_ym", nullable = false, columnDefinition = "bpchar")
    private String firstRegistrationYm;

    @Column(name = "vehicle_type", nullable = false, length = 20)
    private String vehicleType;

    @Column(name = "vehicle_insurance", nullable = false)
    private Boolean vehicleInsurance;

    @Column(name = "annual_premium", nullable = false)
    private Integer annualPremium;

    @Column(name = "monthly_premium", nullable = false)
    private Integer monthlyPremium;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 明細のカスケード保存。orphanRemoval = true の設定は、コレクションから削除されたオブジェクトがデータベースからも物理的に削除されることを意味する。
    @Builder.Default
    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteBreakdown> breakdowns = new ArrayList<>();

    public void addBreakdown(QuoteBreakdown breakdown) {
        breakdowns.add(breakdown);
        breakdown.setQuote(this);
    }
}
