package com.citcd.demo.auth.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.auth.dtos.LoginRequest;
import com.citcd.demo.auth.dtos.LoginResponse;
import com.citcd.demo.auth.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest req) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));

		String token = jwtService.generateAccessToken((UserDetails) auth.getPrincipal());
		return new LoginResponse(token, "Bearer");
	}
}
