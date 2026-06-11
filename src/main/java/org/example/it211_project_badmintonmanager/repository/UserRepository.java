package org.example.it211_project_badmintonmanager.repository;

import org.example.it211_project_badmintonmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Tìm kiếm username có phân trang (Phục vụ FR-05)
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
