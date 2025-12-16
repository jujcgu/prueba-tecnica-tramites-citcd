package com.citcd.demo.auth.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.auth.jwt.JwtService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record LoginResponse(String accessToken, String tokenType) {
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        String token = jwtService
                .generateAccessToken((UserDetails) auth.getPrincipal());
        return new LoginResponse(token, "Bearer");
    }
}
