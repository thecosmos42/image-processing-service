package com.example.image.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${spring.jwt.key}")
    String key;

    public String generateToken(String userName){
        log.debug("Generating token for user: {}", userName);
        if (key == null || key.getBytes().length < 32) {
            throw new IllegalArgumentException("Invalid key");
        }

        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + 1000 * 60 * 60 * 10); // 10 hours validity

        return Jwts.builder()
                .setSubject(userName)
                .setExpiration(expiration)
                .setIssuedAt(issuedAt)
                .signWith(Keys.hmacShaKeyFor(key.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public Boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
