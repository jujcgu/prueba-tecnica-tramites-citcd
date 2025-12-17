package com.citcd.demo.auth.dtos;

public record LoginResponse(String accessToken, Boolean isAdmin) {
}
