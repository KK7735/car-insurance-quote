package com.example.carinsurance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

// Spring MVC のグローバル設定クラス。インターセプタールールとクロスオリジン (CORS) ポリシーを統一的に登録する。これはフロントエンドとバックエンドを分離したアーキテクチャにおける重要なセキュリティ防衛線である。
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // インターセプタールールの設定：/api/admin および /api/master で始まるすべてのパスを保護するが、ログインAPIは除外する（ホワイトリスト）。
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**", "/api/master/**")
                .excludePathPatterns("/api/admin/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
