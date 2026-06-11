package org.example.it211_project_badmintonmanager.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Lấy config từ file application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long expiration;

    // Chuyển đổi chuỗi secret thành khóa mã hóa an toàn
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Hàm tạo Token từ thông tin User
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    // Trích xuất Username từ chuỗi Token
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Dùng verifyWith thay vì setSigningKey
                .build()
                .parseSignedClaims(token)    // Dùng parseSignedClaims thay vì parseClaimsJws
                .getPayload()                // Dùng getPayload thay vì getBody
                .getSubject();
    }

    // Kiểm tra Token còn hạn và có đúng với user hiện tại không
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Kiểm tra thời hạn Token
    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new java.util.Date());
    }
}
