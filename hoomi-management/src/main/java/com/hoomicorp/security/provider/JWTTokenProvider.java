package com.hoomicorp.security.provider;

import com.hoomicorp.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.ttl}")
    private Integer ttl;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(final String username) {
        final Claims claims = Jwts.claims().setSubject(username);
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expires = now.plusMinutes(ttl);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expires.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();


    }

    public boolean validateToken(final String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            if (claims.getExpiration().before(new Date())) {
                return false;
            }

        } catch (Exception e) {
            throw new AuthenticationException("JWT Token is invalid");
        }

        return true;
    }


}
