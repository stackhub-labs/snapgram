package com.instagram.config;

import com.instagram.error.ErrorCode;
import io.jsonwebtoken.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    public JwtInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // OPTIONS 요청(preflight)은 JWT 검증을 건너뜁니다
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": " + ErrorCode.UNAUTHORIZED + ", \"message\": \"인증 토큰이 없습니다.\"}");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecretKey().getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 사용자 정보를 request attribute에 저장
            Long userId = Long.valueOf(claims.get("id").toString());
            request.setAttribute("userId", userId);
            request.setAttribute("userEmail", claims.get("email"));
            request.setAttribute("userNickname", claims.get("nickname"));
            
            return true;
        } catch (JwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": " + ErrorCode.UNAUTHORIZED + ", \"message\": \"유효하지 않은 토큰입니다.\"}");
            return false;
        }
    }
}
