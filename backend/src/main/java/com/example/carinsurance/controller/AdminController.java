package com.example.carinsurance.controller;

import com.example.carinsurance.domain.dto.AdminLoginRequest;
import com.example.carinsurance.domain.dto.AdminLoginResponse;
import com.example.carinsurance.domain.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

// 管理者認証エンドポイント。トークン取得APIへのアクセスにトークンが要求される認証のループを防ぐため、このAPIはインターセプターのホワイトリストに設定する必要がある。
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "管理者認証関連API")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "管理者ログイン", description = "管理者IDとパスワードでログインし、トークンを取得する")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(adminService.login(request));
    }
}
