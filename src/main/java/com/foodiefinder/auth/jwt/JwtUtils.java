package com.foodiefinder.auth.jwt;


import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
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

    private final static Long EXPIRED_MS = 60 * 60 * 1000L;

    public String generateToken(String account) {
         Claims claims = Jwts.claims();
         claims.put("account", account);

         return Jwts.builder()
                 .setClaims(claims)
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_MS))
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
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
    }
}
