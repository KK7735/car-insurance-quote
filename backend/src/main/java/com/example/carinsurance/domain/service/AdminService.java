package com.example.carinsurance.domain.service;

import com.example.carinsurance.domain.dto.AdminLoginRequest;
import com.example.carinsurance.domain.dto.AdminLoginResponse;
import com.example.carinsurance.domain.entity.AdminUser;
import com.example.carinsurance.domain.exception.AdminAuthException;
import com.example.carinsurance.domain.repository.AdminUserRepository;
import com.example.carinsurance.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 管理者のビジネスロジック層。ログイン認証の処理を担当する。現在はシンプルな BCrypt 検証を採用しているが、ビジネス要件が複雑な場合は Spring Security の AuthenticationManager を導入することが可能である。
@Service
public class AdminService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AdminAuthException("ユーザー名またはパスワードが間違っています"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
            throw new AdminAuthException("ユーザー名またはパスワードが間違っています");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AdminLoginResponse(token);
    }
}
