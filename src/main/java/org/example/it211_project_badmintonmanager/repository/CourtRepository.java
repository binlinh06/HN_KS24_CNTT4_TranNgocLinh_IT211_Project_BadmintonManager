package org.example.it211_project_badmintonmanager.repository;

import org.example.it211_project_badmintonmanager.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
}
