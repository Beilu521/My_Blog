package com.example.blog.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String accessToken;  // 访问令牌
    private String refreshToken; // 刷新令牌
    private Long expireIn;       // Access Token 有效期（秒）
}