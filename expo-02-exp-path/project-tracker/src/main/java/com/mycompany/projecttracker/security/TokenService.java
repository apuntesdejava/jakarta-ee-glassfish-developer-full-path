package com.mycompany.projecttracker.security;

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

    /**
     * Demo signing secret used to create HMAC-protected JWT tokens.
     */
    private static final String SECRET_KEY = "MiSuperSecretoParaFirmarTokensJWT_DebeSerLargo";

    /**
     * HMAC key derived from the configured secret.
     */
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * Generates a signed JWT with caller roles and a one-hour expiration.
     *
     * @param username the authenticated caller name
     * @param roles the caller groups to include in the token
     * @return compact signed JWT
     */
    public String generateToken(String username, Set<String> roles) {
        // En una aplicación real, SECRET_KEY debería venir de una variable de entorno o vault.
        return Jwts.builder()
            .subject(username)
            .claim("groups", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key)
            .compact();
    }

    /**
     * Validates a token and returns its subject.
     *
     * @param token compact signed JWT
     * @return authenticated caller name
     */
    public String validateTokenAndGetUser(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    /**
     * Extracts caller roles from a validated token.
     *
     * @param token compact signed JWT
     * @return caller roles included in the token
     */
    public Set<String> getRoles(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        // La claim "groups" se emite como lista y se normaliza a Set para Jakarta Security.
        return Set.copyOf(claims.get("groups", java.util.List.class));
    }
}
