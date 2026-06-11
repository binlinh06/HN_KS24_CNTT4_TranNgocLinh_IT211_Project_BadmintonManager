package org.example.it211_project_badmintonmanager.repository;

import org.example.it211_project_badmintonmanager.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Kiểm tra trùng lịch đặt sân
    boolean existsByCourtIdAndBookingDateAndTimeSlot(Long courtId, LocalDate bookingDate, String timeSlot);
    // Tìm lịch sử đặt sân theo Username và sắp xếp ngày giảm dần
    List<Booking> findByUser_UsernameOrderByBookingDateDesc(String username);
}
