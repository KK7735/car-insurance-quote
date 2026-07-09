package com.example.carinsurance.domain.service;

import com.example.carinsurance.domain.dto.QuoteBreakdownDto;
import com.example.carinsurance.domain.dto.QuoteRequest;
import com.example.carinsurance.domain.dto.QuoteResponse;
import com.example.carinsurance.domain.entity.Quote;
import com.example.carinsurance.domain.exception.ResourceNotFoundException;
import com.example.carinsurance.domain.repository.QuoteRepository;
import com.example.carinsurance.domain.service.calculation.QuoteCalculationCommand;
import com.example.carinsurance.domain.service.calculation.QuoteCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteCalculationService calculationService;
    private final QuoteRepository quoteRepository;

    // 見積もりのコアファサードメソッド。フロントエンドのリクエストを受け取り、計算コマンドを構築して計算エンジンに渡し、ビジネス主キーを生成してJPAカスケード永続化を完了する役割を担う。
    @Transactional
    public QuoteResponse createQuote(QuoteRequest request) {
        Quote quote = new Quote();
        quote.setDriverAge(request.getDriverAge());
        quote.setLicenseColor(request.getLicenseColor());
        quote.setUsageType(request.getUsageType());
        quote.setAnnualMileage(request.getAnnualMileage());
        quote.setDriverRange(request.getDriverRange());
        quote.setHasCurrentInsurance(request.getHasCurrentInsurance());
        quote.setGrade(request.getGrade());
        quote.setAccidentTerm(request.getAccidentTerm());
        quote.setMaker(request.getMaker());
        quote.setCarName(request.getCarName());
        quote.setFirstRegistrationYm(request.getFirstRegistrationYearMonth());
        quote.setVehicleType(request.getVehicleType());
        quote.setVehicleInsurance(request.getVehicleInsurance());

        QuoteCalculationCommand cmd = QuoteCalculationCommand.builder()
                .quote(quote)
                .propertyDamageLimit(request.getPropertyDamageLimit())
                .personalInjuryAmount(request.getPersonalInjuryAmount())
                .lawyerOption(request.getLawyerOption())
                .roadService(request.getRoadService())
                .build();

        // 1. コア計算エンジンの呼び出し
        Quote calculatedQuote = calculationService.calculate(cmd);
        
        // 2. ビジネスフィールドの補完 (見積番号)
        // 見積番号の生成ルール：プレフィックス(EST) + 現在の日付(yyyyMMdd) + 4桁の連番。データベースの count() に依存しているため並行処理時の重複リスクがあるが、今回は課題のサンプルとして実装している。
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = quoteRepository.count() + 1; 
        calculatedQuote.setQuoteNo(String.format("EST%s%04d", dateStr, count));

        // 3. データベースへの保存 (JPA カスケードによる明細の自動保存)
        Quote saved = quoteRepository.save(calculatedQuote);
        
        return mapToResponse(saved);
    }
    
    @Transactional(readOnly = true)
    public QuoteResponse getQuote(String quoteNo) {
        Quote quote = quoteRepository.findByQuoteNo(quoteNo)
                .orElseThrow(() -> new ResourceNotFoundException("見積番号が存在しません: " + quoteNo));
        return mapToResponse(quote);
    }
    
    // 内部エンティティをフロントエンドのDTOに変換する。エンティティ内部の外部キー関係を隠蔽し、フラット化して出力する。
    private QuoteResponse mapToResponse(Quote quote) {
        List<QuoteBreakdownDto> breakdowns = quote.getBreakdowns().stream()
            .map(b -> QuoteBreakdownDto.builder()
                .itemName(b.getItemName())
                .rate(b.getRate())
                .amount(b.getAmount())
                .build())
            .collect(Collectors.toList());
            
        return QuoteResponse.builder()
            .quoteNo(quote.getQuoteNo())
            .annualPremium(quote.getAnnualPremium())
            .monthlyPremium(quote.getMonthlyPremium())
            .breakdowns(breakdowns)
            .createdAt(quote.getCreatedAt())
            .build();
    }
}
