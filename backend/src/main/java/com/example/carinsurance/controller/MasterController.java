package com.example.carinsurance.controller;

import com.example.carinsurance.domain.dto.RateMasterDto;
import com.example.carinsurance.domain.service.RateMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@Tag(name = "Master API", description = "マスタデータ関連API")
public class MasterController {

    private final RateMasterService rateMasterService;

    @GetMapping("/rates")
    @Operation(summary = "料率マスタ取得", description = "有効なすべての料率マスタを取得する")
    public List<RateMasterDto> getRates() {
        return rateMasterService.getActiveRates();
    }
}
