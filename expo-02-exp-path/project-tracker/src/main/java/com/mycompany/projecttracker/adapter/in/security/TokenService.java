package com.mycompany.projecttracker.adapter.in.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    private static final String SECRET_KEY = "MiSuperSecretoParaFirmarTokensJWT_DebeSerLargo";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
            .subject(username)
            .claim("groups", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key)
            .compact();
    }

    public String validateTokenAndGetUser(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    public Set<String> getRoles(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return Set.copyOf(claims.get("groups", java.util.List.class));
    }
}
