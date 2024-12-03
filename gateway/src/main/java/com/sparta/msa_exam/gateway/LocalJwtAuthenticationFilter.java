package com.sparta.msa_exam.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/auth/sign-in") || path.equals("/auth/sign-up")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);

//        if (token == null || !validateToken(token, exchange)) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            log.info("#####Error :: " + token);
//            return exchange.getResponse().setComplete();
//        }

        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        ServerWebExchange mutatedExchange = validateToken(token, exchange);
        if (mutatedExchange == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // 유효하지 않은 경우 처리
        }

        log.info("Final Headers Before Passing to Chain: {}", mutatedExchange.getRequest().getHeaders());
        return chain.filter(mutatedExchange);
    }

    private ServerWebExchange validateToken(String token, ServerWebExchange exchange) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = claimsJws.getBody();

            String username = claims.get("username").toString();
            log.info("Username extracted: {}", username);
            log.info("Exchange Request Headers Before Mutation: {}", exchange.getRequest().getHeaders());

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Username", username)
                    .build();
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            log.info("Exchange Request Headers After Mutation: {}", mutatedExchange.getRequest().getHeaders());
            return mutatedExchange;
        } catch (Exception e) {
            log.error("JWT validation failed", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().setComplete(); // 에러 처리
            return null; // null 반환 시 이후 로직에서 예외 처리
        }
    }

//    private boolean validateToken(String token, ServerWebExchange exchange) {
//        try {
//            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
//            Jws<Claims> claimsJws = Jwts.parser()
//                    .verifyWith(key)
//                    .build().parseSignedClaims(token);
//            log.info("#####payload :: " + claimsJws.getPayload().toString());
//            Claims claims = claimsJws.getBody();
//            log.info("##### username :: " +claims.get("username"));
//            log.info("Exchange Request Headers Before Mutation: {}", exchange.getRequest().getHeaders());
//
//            exchange = exchange.mutate()
//                    .request(exchange.getRequest().mutate()
//                            .header("X-Username", claims.get("username").toString())
//                            .build()).build();
//
//            log.info("Exchange Request Headers After Mutation: {}", exchange.getRequest().getHeaders());
//
//            // 추가적인 검증 로직 (예: 토큰 만료 여부 확인 등)을 여기에 추가할 수 있습니다.
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
