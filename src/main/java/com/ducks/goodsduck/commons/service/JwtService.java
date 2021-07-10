package com.ducks.goodsduck.commons.service;

import com.ducks.goodsduck.commons.model.dto.JwtDto;
import com.ducks.goodsduck.commons.model.dto.UserDto;
import com.ducks.goodsduck.commons.model.entity.User;
import com.ducks.goodsduck.commons.model.enums.UserRole;
import com.ducks.goodsduck.commons.repository.UserRepository;
import com.ducks.goodsduck.commons.util.PropertyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtService {

    private static final String SECRET_KEY = PropertyUtil.getProperty("spring.security.jwt.secret-key");
    private final UserRepository userRepository;

    public String createJwt(JwtDto jwtDto, long ttl) {

        if (ttl <= 0) {
            throw new RuntimeException("Expired time must be greater than Zero : ["+ttl+"] ");
        }

        // 토큰을 서명하기 위해 사용해야할 알고리즘 선택
        SignatureAlgorithm signatureAlgorithm= SignatureAlgorithm.HS256;

        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setSubject(jwtDto.getId().toString())
                .signWith(signingKey, signatureAlgorithm)
                .setExpiration(new Date(System.currentTimeMillis() + ttl))
                .compact();
    }

    public Long getUserIdFromJwt(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // 1. 둘러보기 사용자 체크
    // 2. 만료기간 & 유효 사용자 체크
    public boolean isValidJwt(String jwt) {

        // 1. 둘러보기 사용자 체크
        if(jwt.equals("null")) {
            return false;
        }

        // 2. 만료기간 & 유효 사용자 체크
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return false;
        } catch (SignatureException e) {
            return false;
        }

        return true;
    }

    public String extendJwt(String jwt) {

        JwtDto jwtDto = new JwtDto(getUserIdFromJwt(jwt));
        String renewJwt = createJwt(jwtDto, (20 * 1000 * 60));

        return renewJwt;
    }

    public UserDto isValidAccess(String jwt) {

        if(isValidJwt(jwt) == false) {
            System.out.println("isValidJwt = false, go to login");
            UserDto userDto = new UserDto();
            userDto.setRole(UserRole.ANONYMOUS);
            System.out.println(userDto);

            return userDto;
        }

        Long userId = getUserIdFromJwt(jwt);

        User user = userRepository.findById(userId).get();
        user.setLastLoginAt(LocalDateTime.now());

        UserDto userDto = new UserDto(user);
        userDto.setRole(UserRole.USER);
        userDto.setJwt(extendJwt(jwt));

        return userDto;
    }
}
