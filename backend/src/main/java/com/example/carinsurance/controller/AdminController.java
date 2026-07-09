package com.example.carinsurance.controller;

import com.example.carinsurance.domain.dto.AdminLoginRequest;
import com.example.carinsurance.domain.dto.AdminLoginResponse;
import com.example.carinsurance.domain.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 管理者認証エンドポイント。トークン取得APIへのアクセスにトークンが要求される認証のループを防ぐため、このAPIはインターセプターのホワイトリストに設定する必要がある。
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(adminService.login(request));
    }
}
