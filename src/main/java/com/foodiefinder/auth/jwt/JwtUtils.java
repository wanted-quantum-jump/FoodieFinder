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

    @Value("${jwt.secret.access.key}")
    private String accessSecretKey;
    @Value("${jwt.secret.refresh.key")
    private String refreshSecretKey;


    private final static Long EXPIRED_ACCESS_MS = 60 * 60 * 1000L;
    private final static Long EXPIRED_REFRESH_MS = 60 * 60 * 24 * 1000L;
    private final static String ACCESS_TOKEN = "accessToken";
    private final static String REFRESH_TOKEN = "refreshToken";

    public String[] generateToken(String account) {
         Claims accessClaims = Jwts.claims();
         accessClaims.put("type", ACCESS_TOKEN);
         accessClaims.put("account", account);

         String accessToken = Jwts.builder()
                 .setClaims(accessClaims)
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_ACCESS_MS))
                 .signWith(SignatureAlgorithm.HS512, accessSecretKey)
                 .compact();

        Claims refreshClaims = Jwts.claims();
        refreshClaims.put("type", REFRESH_TOKEN);
        refreshClaims.put("account", account);

        String refreshToken = Jwts.builder()
                .setClaims(refreshClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_REFRESH_MS))
                .signWith(SignatureAlgorithm.HS512, refreshSecretKey)
                .compact();

        String[] tokens = new String[] {accessToken, refreshToken};

        return tokens;

    }

    public String extractAccount(String accessToken) {
         return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(accessToken).getBody().get("account", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Date expiredDate = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getExpiration();
            Date currentDate = new Date();
            return !expiredDate.before(currentDate);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
    }

    public String verifyRefreshTokenAndReissue(String refreshToken) {

        if (refreshToken == null || refreshToken.startsWith("Bearer")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Date expiredDate = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().getExpiration();
        Date currentDate = new Date();

        if (!expiredDate.before(currentDate)) {
            String account = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().get("account", String.class);

            if (account == null || account.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            } else {
                Claims accessClaims = Jwts.claims();
                accessClaims.put("type", ACCESS_TOKEN);
                accessClaims.put("account", account);

                String accessToken = Jwts.builder()
                        .setClaims(accessClaims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_ACCESS_MS))
                        .signWith(SignatureAlgorithm.HS512, accessSecretKey)
                        .compact();

                return accessToken;
            }
        } else {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

    }
}
