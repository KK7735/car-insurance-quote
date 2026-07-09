package com.example.carinsurance.domain.controller;

import com.example.carinsurance.domain.dto.QuoteRequest;
import com.example.carinsurance.domain.dto.QuoteResponse;
import com.example.carinsurance.domain.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// 見積もりシステムのクライアント向けエントリポイントコントローラー。Admin インターセプターによる保護はなく、誰でもアクセス可能である。
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@Tag(name = "Quote API", description = "見積もり関連API")
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "見積作成", description = "入力値を検証し、保険料を計算して保存する")
    public QuoteResponse createQuote(@Valid @RequestBody QuoteRequest request) {
        return quoteService.createQuote(request);
    }

    @GetMapping("/{quoteNo}")
    @Operation(summary = "見積結果取得", description = "保存された見積結果と内訳を取得する")
    public QuoteResponse getQuote(@PathVariable String quoteNo) {
        return quoteService.getQuote(quoteNo);
    }
}
