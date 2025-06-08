package com.swarnlink.util;

import com.swarnlink.config.JwtProperties;
import com.swarnlink.config.JwtUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {


    private final JwtProperties props;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtUtil(JwtProperties props) {
        this.props = props;
        this.privateKey = KeyUtil.loadPrivateKey(props.getPrivateKeyFileName());
        this.publicKey = KeyUtil.loadPublicKey(props.getPublicKeyFileName());
    }

    public String generateToken(Long userId,String shopName, String city, String phoneNumber, String name, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(props.getExpiration())))
                .claim("phoneNumber", phoneNumber)
                .claim("roles", roles)
                .claim("shopName", shopName)
                .claim("city",city)
                .claim("fullName",name)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token); // this will throw if invalid
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public JwtUserPrincipal extractPrincipal(String token) {
        Claims claims = getClaims(token);
        Long userId = Long.valueOf(claims.getSubject());
        String phone = claims.get("phoneNumber", String.class);
        String shopName = claims.get("shopName", String.class);
        String city = claims.get("city", String.class);
        String fullName = claims.get("fullName", String.class);
        List<String> roles = claims.get("roles", List.class);

        return new JwtUserPrincipal(userId,shopName,city, phone, fullName,roles);
    }


    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    public String getFullNamee(String token) {
        return getClaims(token).get("phone", String.class);
    }

    public String getPhone(String token) {
        return getClaims(token).get("phone", String.class);
    }

    public List<String> getRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
