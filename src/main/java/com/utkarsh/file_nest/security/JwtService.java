package com.utkarsh.file_nest.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(String email) {
        return Jwts.builder()
                   .subject(email)
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                   .signWith(signingKey())
                   .compact();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token){
        return Jwts.parser()
                   .verifyWith(signingKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

public boolean isTokenValid(String token, String email){
    String tokenEmail = extractEmail(token);
    return tokenEmail.equals(email) && !isTokenExpired(token);
}

private boolean isTokenExpired(String token) {
    Date expiration = Jwts.parser()
                          .verifyWith(signingKey())
                          .build()
                          .parseSignedClaims(token)
                          .getPayload()
                          .getExpiration();
    return expiration.before(new Date());
}
}
