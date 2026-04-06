package com.deerecho.My_Blog.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expire:7200000}") // 默认2小时
    private Long accessExpire;

    @Value("${jwt.refresh-expire:604800000}") // 默认7天
    private Long refreshExpire;

    // ====================== 常量定义 ======================
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_TYPE = "type";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    // ====================== Token类型枚举 ======================
    public enum TokenType {
        ACCESS,//Access Token
        REFRESH//Refresh Token
    }

    // ====================== 双Token返回对象 ======================
    public static class TokenVO {
        private String accessToken;
        private String refreshToken;

        public TokenVO(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        // getter和setter
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    // ====================== 核心方法 ======================

    /**
     * 一次性生成双Token（最常用）
     */
    public TokenVO generateTokenPair(Long userId) {
        return generateTokenPair(userId, null);
    }

    public TokenVO generateTokenPair(Long userId, String username) {
        String accessToken = createAccessToken(userId, username);
        String refreshToken = createRefreshToken(userId);
        return new TokenVO(accessToken, refreshToken);
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, null, accessExpire, TokenType.ACCESS);
    }

    public String createAccessToken(Long userId, String username) {
        return createToken(userId, username, accessExpire, TokenType.ACCESS);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, null, refreshExpire, TokenType.REFRESH);
    }

    public String createToken(Long userId, String username) {
        return createToken(userId, username, accessExpire, TokenType.ACCESS);
    }

    /**
     * 构建Token的核心私有方法
     */
    private String createToken(Long userId, String username, long expireTime, TokenType tokenType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put(KEY_USER_ID, userId);
        claims.put(KEY_TYPE, tokenType.name());
        if (username != null) {
            claims.put("username", username);
        }

        return Jwts.builder()
                .setClaims(claims) 
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SIGNATURE_ALGORITHM) // 显式指定签名算法
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder() 
                .setSigningKey(getSigningKey()) 
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ====================== 工具方法 ======================

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get(KEY_USER_ID);
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public TokenType getTokenType(String token) {
        Claims claims = parseToken(token);
        String type = claims.get(KEY_TYPE, String.class);
        return TokenType.valueOf(type);
    }

    /**
     * 验证Token是否有效（签名正确+未过期）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isAccessToken(String token) {
        return getTokenType(token) == TokenType.ACCESS;
    }

    public boolean isRefreshToken(String token) {
        return getTokenType(token) == TokenType.REFRESH;
    }

    public long getRemainingTime(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getAccessExpire() {
        return accessExpire;
    }

    public long getRefreshExpire() {
        return refreshExpire;
    }

    // ====================== 私有工具方法 ======================
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}