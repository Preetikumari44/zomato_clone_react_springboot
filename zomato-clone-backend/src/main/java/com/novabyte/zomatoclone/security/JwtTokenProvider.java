package com.novabyte.zomatoclone.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.novabyte.zomatoclone.common.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Issues and parses JWTs.
 *
 * Design decision: the token carries a SINGLE "activeRole" claim, not the
 * full set of roles as authorities. RBAC checks (hasRole in SecurityConfig,
 * @PreAuthorize on controllers) are evaluated against activeRole only.
 * This means a user with both CUSTOMER and DELIVERY_PARTNER roles cannot
 * accidentally (or maliciously) act as a delivery partner on a token that
 * was issued for the customer role — they must explicitly call
 * POST /api/auth/switch-role to get a token scoped to the other role.
 * "availableRoles" is also embedded so the frontend can render the
 * role-switcher without an extra API call.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                             @Value("${app.jwt.expiration-ms}") long expirationMs) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes (256 bits) long");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String email, Role activeRole, Set<Role> availableRoles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        List<String> roleNames = availableRoles.stream().map(Enum::name).collect(Collectors.toList());

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("activeRole", activeRole.name())
                .claim("availableRoles", roleNames)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public Long getUserId(Claims claims) {
        return claims.get("userId", Long.class) != null
                ? Long.valueOf(claims.get("userId").toString())
                : null;
    }

    public Role getActiveRole(Claims claims) {
        return Role.valueOf(claims.get("activeRole", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> getAvailableRoles(Claims claims) {
        return (List<String>) claims.get("availableRoles", List.class);
    }
}
