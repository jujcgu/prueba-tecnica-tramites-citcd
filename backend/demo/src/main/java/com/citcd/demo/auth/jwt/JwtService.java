package com.citcd.demo.auth.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtService {

    private final JwtProperties props;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.algorithm = Algorithm.HMAC256(props.secret());
        this.verifier = JWT.require(algorithm)
                .withIssuer(props.issuer())
                .build();
    }

    public String generateAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.accessTokenMinutes() * 60);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JWT.create()
                .withIssuer(props.issuer())
                .withSubject(userDetails.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }

}
