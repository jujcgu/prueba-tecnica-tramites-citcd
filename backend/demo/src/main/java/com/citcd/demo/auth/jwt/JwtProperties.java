package com.citcd.demo.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo.jwt")
public record JwtProperties(
        String issuer,
        String secret,
        long accessTokenMinutes) {
}
