package com.example.carinsurance.domain.service.calculation;

import com.example.carinsurance.domain.entity.Quote;
import com.example.carinsurance.domain.entity.RateMaster;
import com.example.carinsurance.domain.repository.RateMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
// 見積もり計算のコアオーケストレーションサービス。具体的な計算ロジックは含まず、料率マスタのロード、コンテキストの初期化、指定された順序での各ステップのトリガーのみを担当する。
public class QuoteCalculationService {

    private final RateMasterRepository rateMasterRepository;
    private final List<CalculationStep> steps;

    public Quote calculate(QuoteCalculationCommand command) {
        List<RateMaster> rateMasters = rateMasterRepository.findByActiveTrue();
        QuoteContext context = new QuoteContext(command, rateMasters);

        // 1. 基本保険料を登録
        context.addBaseBreakdown();

        // 2. 各係数・加算額を順番に適用
        // 早期の乗数係数が後続ステップの基準値に影響を与えるため、getOrder() の昇順で実行する必要がある。実行順序はコアな業務制約である。
        steps.stream()
             .sorted(Comparator.comparingInt(CalculationStep::getOrder))
             .forEach(step -> step.calculate(context));

        // 3. 四捨五入などの端数処理と最終セット
        return context.finalizeQuote();
    }
}
