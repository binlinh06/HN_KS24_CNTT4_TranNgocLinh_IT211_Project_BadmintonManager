package org.example.it211_project_badmintonmanager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BookingResponseDTO {
    private Long id;
    private Long courtId;
    // Nếu bảng Booking của bạn có liên kết lấy được tên sân, bạn có thể thêm: private String courtName;
    private LocalDate bookingDate;
    private String timeSlot;
    private String status; // Ví dụ: "PENDING", "CONFIRMED", "CANCELLED"
}