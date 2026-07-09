package com.example.carinsurance.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    // JWTシークレットと有効期限の設定。ハードコーディングは課題のための簡略化であり、本番環境では必ず外部化する（AWS Secrets ManagerやVaultなど）。24時間の有効期限は社内管理者向けには適しているが、リフレッシュメカニズムは欠けている。
    // 実際の運用では application.yml に配置すべきであるが、ここでは課題のため一時的にハードコーディングしている
    private static final String SECRET = "MySuperSecretKeyForCarInsuranceApp1234567890";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    // 署名の検証と解析を行う。トークンが改ざんされている、期限切れである、または署名が一致しない場合、jjwtは例外をスローする。ここではそれらを一括してキャッチしnullを返し、インターセプターに処理を委ねる。
    public String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null; // 解析に失敗した場合は null を返す
        }
    }
}
