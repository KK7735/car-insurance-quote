package com.example.carinsurance.controller;

import com.example.carinsurance.domain.dto.AdminQuoteDto;
import com.example.carinsurance.domain.dto.QuoteResponse;
import com.example.carinsurance.domain.service.AdminQuoteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 管理者専用の見積もり管理API。パスはインターセプターで設定されたパターン（例: /api/admin/**）と一致する必要があり、有効なトークンの付与が強制される。
@RestController
@RequestMapping("/api/admin/quotes")
public class AdminQuoteController {
    
    @Autowired
    private AdminQuoteService adminQuoteService;

    @GetMapping
    public ResponseEntity<Page<AdminQuoteDto>> getQuotes(
            @RequestParam(required = false) String quoteNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminQuoteService.getQuotes(quoteNo, page, size));
    }

    @GetMapping("/{quoteNo}")
    public ResponseEntity<QuoteResponse> getQuoteDetail(@PathVariable String quoteNo) {
        return ResponseEntity.ok(adminQuoteService.getQuoteDetail(quoteNo));
    }

    @GetMapping(value = "/csv", produces = "text/csv; charset=UTF-8")
    public void downloadCsv(HttpServletResponse response) {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"quotes.csv\"");
        adminQuoteService.exportCsv(response);
    }
}
