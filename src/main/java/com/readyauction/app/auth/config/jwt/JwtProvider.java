package com.readyauction.app.auth.config.jwt;

import com.readyauction.app.user.entity.Admin;
import com.readyauction.app.user.entity.Authority;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.SpecialOneToOneType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class JwtProvider {
    private final JwtProperties jwtProperties;

    public static final User EMPTY_MEMBER = Member.builder().build();
    public static final User EMPTY_ADMIN = Admin.builder().build();
    private final long accessTokenExpirationTime = 1000L * 60 * 30;
    private final long refreshTokenExpirationTime = 1000L * 60 * 60 * 24 * 30;

    public String createAccessToken(User user) {
        return createToken(accessTokenExpirationTime, user);
    }
    public String createRefreshToken(Authority authority) {
        if (authority.equals(Authority.ROLE_USER))
            return createToken(refreshTokenExpirationTime, EMPTY_MEMBER);
        else return createToken(refreshTokenExpirationTime, EMPTY_ADMIN);
    }

    private String createToken(Long validityInMilliseconds, User user) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(validity)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getAuthorities())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public boolean validateAccessToken(final String accessToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(accessToken);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(claims.get("role").toString())
        );

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(
                claims.getSubject(), "", authorities), token, authorities);
    }

    private Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

}
