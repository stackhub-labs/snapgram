package com.instagram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtProperties {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String getSecretKey() {
        return secretKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}
