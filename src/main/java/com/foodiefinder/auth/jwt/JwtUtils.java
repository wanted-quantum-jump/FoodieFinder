package com.foodiefinder.auth.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

     public String generateToken(String account, Long expiredMs) {
         Claims claims = Jwts.claims();
         claims.put("account", account);

         return Jwts.builder()
                 .setClaims(claims)
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                 .signWith(SignatureAlgorithm.HS512, secretKey)
                 .compact();
     }

     public String extractAccount(String token) {
         return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("account", String.class);
     }

    public boolean isTokenValid(String token) {
        try {
            Date expiredDate = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
            Date currentDate = new Date();
            return !expiredDate.before(currentDate);
        } catch (JwtException e) {
            //todo JWT 검증 에러 처리
            return false;
        }
    }
}
