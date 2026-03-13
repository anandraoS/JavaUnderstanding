package com.learning.common_library.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * ═══════════════════════════════════════════════════════════════════
 * JWT UTILITY — Token Generation & Validation
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: JWT authentication, JJWT 0.12.x API, Security
 *
 * CONCEPT — WHAT IS JWT?
 *   JWT = JSON Web Token = a signed JSON object
 *   Structure: HEADER.PAYLOAD.SIGNATURE (three Base64-encoded parts)
 *
 *   HEADER:    { "alg": "HS512", "typ": "JWT" }
 *   PAYLOAD:   { "sub": "john", "role": "ROLE_USER", "iat": ..., "exp": ... }
 *   SIGNATURE: HMACSHA512(base64(header) + "." + base64(payload), SECRET_KEY)
 *
 * CONCEPT — WHY HS512?
 *   HS256 = 256-bit key (32 bytes) — secure but shorter
 *   HS512 = 512-bit key (64 bytes) — more secure, harder to brute-force
 *   The secret key MUST be at least 64 bytes for HS512
 *
 * JJWT 0.12.x API CHANGES (vs older versions):
 *   OLD: Jwts.parserBuilder().setSigningKey(key).build()
 *   NEW: Jwts.parser().verifyWith(key).build()
 *
 *   OLD: SignatureAlgorithm.HS512
 *   NEW: Jwts.SIG.HS512
 *
 *   OLD: .setClaims(map).setSubject(sub)
 *   NEW: .subject(sub).claims().add(map).and()
 */
@Component
public class JwtUtil {

    /**
     * CONCEPT — Secret Key:
     *   This key is used to SIGN and VERIFY tokens.
     *   Anyone who has this key can create valid tokens!
     *   In production: store in environment variable, Vault, or K8s secret.
     *   NEVER commit real keys to source control.
     *
     *   The same key must be used by:
     *   - user-service (to GENERATE tokens on login)
     *   - api-gateway (to VALIDATE tokens on every request)
     */
    private static final String SECRET_KEY =
            "MySecretKeyForJWTTokenGenerationMustBeLongEnoughFor512BitHS512AlgorithmRequirement";
    private static final long JWT_TOKEN_VALIDITY = 5L * 60 * 60 * 1000; // 5 hours

    private final SecretKey key;

    public JwtUtil() {
        // Use StandardCharsets.UTF_8 to ensure consistent byte encoding across all platforms
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // ─── Extract claims ────────────────────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ─── Generate token ────────────────────────────────────────────────────────

    /**
     * PSEUDOCODE — Token generation:
     *   Input:  username = "john", role = "ROLE_USER"
     *   Step 1: Set subject (sub) = "john"
     *   Step 2: Add custom claims: { "role": "ROLE_USER" }
     *   Step 3: Set issued-at (iat) = now
     *   Step 4: Set expiration (exp) = now + 5 hours
     *   Step 5: Sign with HS512 algorithm using secret key
     *   Output: "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huIi..."
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(subject)                        // MUST set subject FIRST
                .claims()                                // then open claims builder
                    .add(claims)                         // add custom claims (role, etc.)
                    .and()                               // close claims builder
                .issuedAt(new Date(now))
                .expiration(new Date(now + JWT_TOKEN_VALIDITY))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // ─── Validate token ────────────────────────────────────────────────────────

    /**
     * PSEUDOCODE — Token validation:
     *   1. Parse token → verify HMAC signature with secret key
     *      → Signature mismatch? → SecurityException → return false
     *   2. Extract username from "sub" claim
     *      → Matches expected username? → YES → continue
     *   3. Check expiration from "exp" claim
     *      → exp < now? → token expired → return false
     *   4. All checks pass → return true
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return extractedUsername != null
                    && extractedUsername.equals(username)
                    && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            // Token has expired
            return false;
        } catch (SecurityException | MalformedJwtException e) {
            // Token signature is invalid or token is malformed
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
