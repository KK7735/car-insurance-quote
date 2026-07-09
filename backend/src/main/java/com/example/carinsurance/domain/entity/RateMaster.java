package com.example.carinsurance.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

// 料率マスタのエンティティ。これはデータ駆動設計のコアであり、すべての乗数 (rate) と定額加算 (amount) はこのテーブルで設定されるため、計算ルールを変更する際にコードを修正する必要がない。
@Entity
@Table(name = "rate_masters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "item_code", nullable = false, length = 50)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "rate", precision = 6, scale = 3)
    private BigDecimal rate;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
