package org.example.it211_project_badmintonmanager.repository;

import org.example.it211_project_badmintonmanager.entity.CourtImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtImageRepository extends JpaRepository<CourtImage, Long> {
    // Tìm tất cả ảnh của một sân cụ thể
    List<CourtImage> findByCourtId(Long courtId);
}