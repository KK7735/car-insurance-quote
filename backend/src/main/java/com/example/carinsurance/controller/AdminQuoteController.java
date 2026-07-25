package com.example.carinsurance.controller;

import com.example.carinsurance.domain.dto.AdminQuoteDto;
import com.example.carinsurance.domain.dto.QuoteResponse;
import com.example.carinsurance.domain.service.AdminQuoteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

// 管理者専用の見積もり管理API。パスはインターセプターで設定されたパターン（例: /api/admin/**）と一致する必要があり、有効なトークンの付与が強制される。
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Quote API", description = "管理者用見積もり関連API")
public class AdminQuoteController {
    
    @Autowired
    private AdminQuoteService adminQuoteService;

    @GetMapping("/quotes")
    @Operation(summary = "見積一覧検索", description = "見積番号による検索とページネーションをサポートする見積一覧を取得する")
    public ResponseEntity<Page<AdminQuoteDto>> getQuotes(
            @RequestParam(required = false) String quoteNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminQuoteService.getQuotes(quoteNo, page, size));
    }

    @GetMapping("/quotes/{quoteNo}")
    @Operation(summary = "管理者用見積詳細", description = "見積番号から見積詳細と内訳を取得する")
    public ResponseEntity<QuoteResponse> getQuoteDetail(@PathVariable String quoteNo) {
        return ResponseEntity.ok(adminQuoteService.getQuoteDetail(quoteNo));
    }

    @GetMapping(value = "/quotes.csv", produces = "text/csv; charset=UTF-8")
    @Operation(summary = "見積一覧CSV出力", description = "すべての見積データをCSV形式でダウンロードする")
    public void downloadCsv(HttpServletResponse response) {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"quotes.csv\"");
        adminQuoteService.exportCsv(response);
    }
}
