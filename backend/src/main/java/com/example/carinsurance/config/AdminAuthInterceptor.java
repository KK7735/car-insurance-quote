package com.example.carinsurance.config;

import com.example.carinsurance.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 管理者向けAPIの認証インターセプター。リクエストから Bearer トークンを抽出し、署名検証に成功した場合、下流のコントローラーで使用するために username を Request 属性に注入する。
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS プリフライトリクエスト (OPTIONS) を許可する。そうしないと、フロントエンドからのトークン付きのクロスオリジンリクエストが直接ブロックされてしまう。
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.validateTokenAndGetUsername(token);
            if (username != null) {
                request.setAttribute("adminUsername", username);
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"UNAUTHORIZED\"}");
        return false;
    }
}
