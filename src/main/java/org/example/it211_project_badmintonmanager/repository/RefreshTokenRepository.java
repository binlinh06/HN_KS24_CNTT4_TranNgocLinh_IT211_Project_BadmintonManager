package org.example.it211_project_badmintonmanager.repository;

import org.example.it211_project_badmintonmanager.entity.RefreshToken;
import org.example.it211_project_badmintonmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    // Thêm hàm này để tìm token theo User
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
}