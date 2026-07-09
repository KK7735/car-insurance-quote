package com.example.carinsurance.domain.service.calculation;

import com.example.carinsurance.domain.entity.Quote;
import com.example.carinsurance.domain.entity.QuoteBreakdown;
import com.example.carinsurance.domain.entity.RateMaster;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

// 計算エンジンのコンテキストオブジェクト。責任チェーン全体で引き回され、保険料・加算額の累積とすべての費用明細の収集を担う。
public class QuoteContext {
    @Getter
    private final QuoteCalculationCommand command;
    private final List<RateMaster> rateMasters;

    private BigDecimal currentPremium = new BigDecimal("50000");
    private int additionAmount = 0;
    private int orderCounter = 1;

    @Getter
    private final List<QuoteBreakdown> breakdowns = new ArrayList<>();

    public QuoteContext(QuoteCalculationCommand command, List<RateMaster> rateMasters) {
        this.command = command;
        this.rateMasters = rateMasters;
    }

    public void addBaseBreakdown() {
        QuoteBreakdown base = QuoteBreakdown.builder()
                .itemCode("BASE")
                .itemName("基本保険料")
                .amount(50000)
                .displayOrder(orderCounter++)
                .build();
        breakdowns.add(base);
    }

    // 乗算タイプの料率を適用する。適用ごとに基本保険料に係数が直接掛けられ、複利効果が生じる。
    public void applyRate(String category, String itemCode) {
        RateMaster master = getMaster(category, itemCode);
        if (master != null && master.getRate() != null) {
            currentPremium = currentPremium.multiply(master.getRate());
            addBreakdown(master);
        }
    }

    public void applyAddition(String category, String itemCode) {
        RateMaster master = getMaster(category, itemCode);
        if (master != null && master.getAmount() != null) {
            additionAmount += master.getAmount();
            addBreakdown(master);
        }
    }

    private RateMaster getMaster(String category, String itemCode) {
        return rateMasters.stream()
                .filter(r -> r.getCategory().equals(category) && r.getItemCode().equals(itemCode))
                .findFirst().orElse(null);
    }

    private void addBreakdown(RateMaster master) {
        QuoteBreakdown breakdown = QuoteBreakdown.builder()
                .itemCode(master.getItemCode())
                .itemName(master.getItemName())
                .rate(master.getRate())
                .amount(master.getAmount())
                .displayOrder(orderCounter++)
                .build();
        breakdowns.add(breakdown);
    }

    public Quote finalizeQuote() {
        // 3. 年間保険料は10円未満を四捨五入する。
        // すべての乗数係数の累積結果と定額加算結果をマージする。setScale(-1) を使用して「10円未満の四捨五入」を実現する（例：12345 -> 12350）。
        BigDecimal annual = currentPremium.add(new BigDecimal(additionAmount));
        annual = annual.setScale(-1, RoundingMode.HALF_UP);

        // 4. 月額保険料は年間保険料÷12を行い、10円未満を四捨五入する。
        // 12で割る際、無限小数による ArithmeticException を防ぐため、まず十分な精度（ここでは3）を指定し、その後 -1 桁の四捨五入を行う必要がある。
        BigDecimal monthly = annual.divide(new BigDecimal("12"), 3, RoundingMode.HALF_UP)
                .setScale(-1, RoundingMode.HALF_UP);

        Quote quote = command.getQuote();
        quote.setAnnualPremium(annual.intValue());
        quote.setMonthlyPremium(monthly.intValue());

        breakdowns.forEach(quote::addBreakdown);

        return quote;
    }
}
