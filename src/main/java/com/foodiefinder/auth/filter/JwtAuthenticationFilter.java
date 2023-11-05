package com.foodiefinder.auth.filter;

import com.foodiefinder.auth.jwt.JwtUtils;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = request.getHeader("Authorization").substring(7);
        String account = jwtUtils.extractAccount(jwt);

        if (account != null && !account.isEmpty()) {
            userRepository.findByAccount(account).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            if (jwtUtils.isTokenValid(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(account, null, List.of(new SimpleGrantedAuthority("USER")));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                //todo JWT 유효성 검사 실패 로직 구현하기
            }
        }
        filterChain.doFilter(request,response);
    }
}
