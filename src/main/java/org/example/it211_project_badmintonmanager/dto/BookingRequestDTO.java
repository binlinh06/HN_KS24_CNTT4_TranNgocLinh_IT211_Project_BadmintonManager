package org.example.it211_project_badmintonmanager.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class BookingRequestDTO {
    private Long courtId;
    private LocalDate bookingDate;
    private String timeSlot;
}