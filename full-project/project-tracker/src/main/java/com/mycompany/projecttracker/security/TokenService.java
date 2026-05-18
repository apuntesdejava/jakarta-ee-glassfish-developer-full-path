package com.mycompany.projecttracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

/**
 * Creates and validates signed JWT access tokens.
 */
@ApplicationScoped
public class TokenService {

    /** Shared signing secret used to derive the HMAC key. */
    private static final String SECRET_KEY = "MiSuperSecretoParaFirmarTokensJWT_DebeSerLargo";

    /** HMAC key used to sign and verify JWTs. */
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * Generates a signed JWT with roles and expiration.
     *
     * @param username subject stored in the token
     * @param roles groups granted to the subject
     * @return compact signed JWT
     */
    public String generateToken(String username, Set<String> roles) {
        /*
         * El token incluye los grupos como claim para que el mecanismo de autenticación pueda
         * reconstruir los roles en cada petición REST.
         */
        return Jwts.builder()
            .subject(username)
            .claim("groups", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key)
            .compact();
    }

    /**
     * Validates a token and extracts its subject.
     *
     * @param token compact JWT to validate
     * @return username stored as the subject
     */
    public String validateTokenAndGetUser(String token) {
        /*
         * La librería verifica firma y expiración durante el parseo; cualquier token inválido
         * produce una excepción para el llamador.
         */
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    /**
     * Extracts application roles from a token.
     *
     * @param token compact JWT to inspect
     * @return roles stored in the groups claim
     */
    public Set<String> getRoles(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        /*
         * El claim groups se serializa como lista; se copia a Set para ajustarse al contrato de
         * Jakarta Security.
         */
        return Set.copyOf(claims.get("groups", java.util.List.class));
    }
}
