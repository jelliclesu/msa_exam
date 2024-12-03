package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.core.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Slf4j
public class AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * AuthService 생성자.
     * Base64 URL 인코딩된 비밀 키를 디코딩하여 HMAC-SHA 알고리즘에 적합한 SecretKey 객체를 생성합니다.
     *
     * @param secretKey Base64 URL 인코딩된 비밀 키
     */
    public AuthService(@Value("${service.jwt.secret-key}") String secretKey,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 사용자 ID를 받아 JWT 액세스 토큰을 생성합니다.
     *
     * @param username 사용자 ID
     * @return 생성된 JWT 액세스 토큰
     */
    public String createAccessToken(String username) {
        log.info("##### Auth Username :: " + username);
        return Jwts.builder()
                .claim("username", username)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 사용자 등록
     *
     * @param user 사용자 정보
     * @return 저장된 사용자
     */
    public User signUp(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * 사용자 인증
     *
     * @param username 사용자 ID
     * @param password 비밀번호
     * @return JWT 액세스 토큰
     */
    public String signIn(String username, String password) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return createAccessToken(user.getUsername());
    }
}
