package org.example.it211_project_badmintonmanager.controller;

import org.example.it211_project_badmintonmanager.dto.BookingResponseDTO;
import org.example.it211_project_badmintonmanager.dto.BookingStatusUpdateDTO;
import org.example.it211_project_badmintonmanager.dto.ResponseDTO;
import org.example.it211_project_badmintonmanager.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/bookings")
public class ManagerBookingController {

    @Autowired
    private BookingService bookingService;

    // UC-08: Cập nhật trạng thái đơn đặt sân
    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseDTO<BookingResponseDTO>> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody BookingStatusUpdateDTO statusDTO) {
        try {
            BookingResponseDTO updatedBooking = bookingService.updateBookingStatus(id, statusDTO.getStatus());

            return ResponseEntity.ok(
                    ResponseDTO.<BookingResponseDTO>builder()
                            .success(true)
                            .message("Cập nhật trạng thái thành công!")
                            .data(updatedBooking)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<BookingResponseDTO>builder()
                            .success(false)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }
}