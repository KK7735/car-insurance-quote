package com.example.carinsurance.domain.service;

import com.example.carinsurance.domain.dto.AdminQuoteDto;
import com.example.carinsurance.domain.dto.QuoteBreakdownDto;
import com.example.carinsurance.domain.dto.QuoteResponse;
import com.example.carinsurance.domain.entity.Quote;
import com.example.carinsurance.domain.exception.ResourceNotFoundException;
import com.example.carinsurance.domain.repository.QuoteRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

// 管理者向けの見積もり管理サービス。ページネーション付きの検索と詳細閲覧機能、および全データの CSV エクスポート機能を提供する。
@Service
public class AdminQuoteService {

    @Autowired
    private QuoteRepository quoteRepository;

    public Page<AdminQuoteDto> getQuotes(String quoteNo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Quote> quotePage;
        
        if (quoteNo != null && !quoteNo.isEmpty()) {
            // Simplified search by quoteNo (exact match)
            quotePage = quoteRepository.findByQuoteNo(quoteNo).map(q -> new org.springframework.data.domain.PageImpl<>(List.of(q), pageable, 1))
                    .orElse(new org.springframework.data.domain.PageImpl<>(List.of(), pageable, 0));
        } else {
            quotePage = quoteRepository.findAll(pageable);
        }

        return quotePage.map(this::mapToDto);
    }

    public QuoteResponse getQuoteDetail(String quoteNo) {
        Quote quote = quoteRepository.findByQuoteNo(quoteNo)
                .orElseThrow(() -> new ResourceNotFoundException("見積番号が見つかりません: " + quoteNo));

        List<QuoteBreakdownDto> breakdownDtos = quote.getBreakdowns().stream()
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
                .breakdowns(breakdownDtos)
                .createdAt(quote.getCreatedAt())
                .build();
    }

    public void exportCsv(HttpServletResponse response) {
        try {
            // UTF-8 with BOM for Excel compatibility
            // エクスポートした CSV を Excel で直接開いた際の日本語の文字化けを防ぐため、UTF-8 BOM ヘッダーを書き込む必要がある。また、ストリーム書き込みにより OOM（メモリ不足）を防ぐことができる。
            byte[] bom = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};
            response.getOutputStream().write(bom);
            
            java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(response.getOutputStream(), java.nio.charset.StandardCharsets.UTF_8));
            writer.println("見積番号,運転者年齢,メーカー,車名,年間保険料,作成日時");

            List<Quote> quotes = quoteRepository.findAll(Sort.by("createdAt").descending());
            for (Quote q : quotes) {
                writer.printf("%s,%d,%s,%s,%d,%s\n",
                        q.getQuoteNo(),
                        q.getDriverAge(),
                        q.getMaker(),
                        q.getCarName(),
                        q.getAnnualPremium(),
                        q.getCreatedAt().toString()
                );
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("CSV出力に失敗しました", e);
        }
    }

    private AdminQuoteDto mapToDto(Quote quote) {
        return AdminQuoteDto.builder()
                .quoteNo(quote.getQuoteNo())
                .driverAge(quote.getDriverAge())
                .licenseColor(quote.getLicenseColor())
                .usageType(quote.getUsageType())
                .annualMileage(quote.getAnnualMileage())
                .driverRange(quote.getDriverRange())
                .hasCurrentInsurance(quote.getHasCurrentInsurance())
                .grade(quote.getGrade())
                .accidentTerm(quote.getAccidentTerm())
                .maker(quote.getMaker())
                .carName(quote.getCarName())
                .firstRegistrationYm(quote.getFirstRegistrationYm())
                .vehicleType(quote.getVehicleType())
                .vehicleInsurance(quote.getVehicleInsurance())
                .annualPremium(quote.getAnnualPremium())
                .monthlyPremium(quote.getMonthlyPremium())
                .createdAt(quote.getCreatedAt())
                .build();
    }
}
