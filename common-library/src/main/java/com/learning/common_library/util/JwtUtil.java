package com.learning.common_library.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility for token generation and validation
 * Demonstrates: JWT authentication, Security
 *
 * JJWT 0.12.x API:
 *   - Jwts.builder()  (unchanged)
 *   - Jwts.parser().verifyWith(key).build()  (replaces parserBuilder/setSigningKey)
 *   - Jwts.SIG.HS512  (replaces deprecated SignatureAlgorithm.HS512)
 */
@Component
public class JwtUtil {

    // In production, load this from config/vault
    private static final String SECRET_KEY =
            "MySecretKeyForJWTTokenGenerationMustBeLongEnoughFor512BitHS512AlgorithmRequirement";
    private static final long JWT_TOKEN_VALIDITY = 5L * 60 * 60 * 1000; // 5 hours

    private final SecretKey key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ─── Extract claims ────────────────────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        // JJWT 0.12.x: use parser().verifyWith(key).build()
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ─── Generate token ────────────────────────────────────────────────────────

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                          // JJWT 0.12.x: .claims() replaces .setClaims()
                .subject(subject)                        // .subject() replaces .setSubject()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key, Jwts.SIG.HS512)          // Jwts.SIG.HS512 replaces SignatureAlgorithm.HS512
                .compact();
    }

    // ─── Validate token ────────────────────────────────────────────────────────

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}
