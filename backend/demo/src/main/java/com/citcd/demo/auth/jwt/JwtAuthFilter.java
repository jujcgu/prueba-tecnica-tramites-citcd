package com.citcd.demo.auth.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String auth = request.getHeader("Authorization");
		if (auth == null || !auth.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		String token = auth.substring(7);

		try {
			DecodedJWT jwt = jwtService.verify(token);
			String username = jwt.getSubject();

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails user = userDetailsService.loadUserByUsername(username);

				String[] roles = jwt.getClaim("roles").asArray(String.class);
				List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
						: Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();

				var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			chain.doFilter(request, response);
		} catch (JWTVerificationException ex) {
			response.setStatus(401);
			response.setContentType("application/json");
			response.getWriter().write("{\"message\":\"Token inválido o expirado\"}");
			return;
		}
	}
}
