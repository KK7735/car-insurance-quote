package com.example.carinsurance.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

// 見積もり明細のエンティティ。保険料に影響を与える具体的な要因（例：年齢係数、特約加算）を記録し、その display_order が管理画面でのレンダリング順序を決定する。
@Entity
@Table(name = "quote_breakdowns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "item_code", nullable = false, length = 50)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "rate", precision = 6, scale = 3)
    private BigDecimal rate;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
}
