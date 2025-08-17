package ru.vgerasimov.OTPCode.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.expiration:86400000}")
    private long JwtTtl;
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);


    public String generateJWT(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtTtl))
                .signWith(key)
                .compact();
    }


    public String extractUserName(String jwt) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt).getBody().getSubject();
    }


    public boolean isTokenValid(String jwt) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
    return false;
    }


}
