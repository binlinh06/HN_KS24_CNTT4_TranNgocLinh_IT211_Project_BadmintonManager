package org.example.it211_project_badmintonmanager.repository;

import org.example.it211_project_badmintonmanager.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByToken(String token); // Hàm kiểm tra xem token có nằm trong sổ đen không
}