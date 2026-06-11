package org.example.it211_project_badmintonmanager.service;

import org.example.it211_project_badmintonmanager.entity.RefreshToken;
import org.example.it211_project_badmintonmanager.entity.User;
import org.example.it211_project_badmintonmanager.repository.RefreshTokenRepository;
import org.example.it211_project_badmintonmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // Thời gian sống của Refresh Token (Ví dụ: 7 ngày = 604800000 ms)
    private final Long refreshTokenDurationMs = 604800000L;

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chiến thuật mới: Tìm xem user này đã có token trong DB chưa.
        // Nếu có rồi thì lấy ra dùng lại, nếu chưa có thì tạo mới (new RefreshToken).
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        // Cập nhật lại thông tin (dù là token cũ hay mới thì đều được cấp chuỗi và hạn mới)
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        // Lưu xuống Database (Spring Boot sẽ tự hiểu: Nếu có ID rồi thì UPDATE, chưa có thì INSERT)
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại!");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }
}