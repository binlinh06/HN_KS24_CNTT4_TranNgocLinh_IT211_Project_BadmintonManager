package org.example.it211_project_badmintonmanager.controller;

import org.example.it211_project_badmintonmanager.dto.BookingRequestDTO;
import org.example.it211_project_badmintonmanager.dto.BookingResponseDTO;
import org.example.it211_project_badmintonmanager.dto.ResponseDTO;
import org.example.it211_project_badmintonmanager.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // UC-06: Khách hàng tạo lịch đặt sân
    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> createBooking(@RequestBody BookingRequestDTO requestDTO) {
        try {
            // Lấy trực tiếp username từ Token xác thực của Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            bookingService.createBooking(requestDTO, currentUsername);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ResponseDTO.<Void>builder().success(true).message("Lịch đặt sân đã được tạo và đang chờ xác nhận").build()
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body( // Trùng lịch -> HTTP 409
                    ResponseDTO.<Void>builder().success(false).message(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( // Lỗi logic -> HTTP 400
                    ResponseDTO.<Void>builder().success(false).message(e.getMessage()).build()
            );
        }
    }
    // UC-07: Xem lịch sử đặt sân của chính mình
    @GetMapping
    public ResponseEntity<ResponseDTO<List<BookingResponseDTO>>> getMyBookingHistory() {
        try {
            // Tự động lấy Username từ Token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            // Gọi Service lấy dữ liệu
            List<BookingResponseDTO> history = bookingService.getBookingHistory(currentUsername);

            return ResponseEntity.ok(
                    ResponseDTO.<List<BookingResponseDTO>>builder()
                            .success(true)
                            .message("Lấy lịch sử đặt sân thành công")
                            .data(history)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<List<BookingResponseDTO>>builder()
                            .success(false)
                            .message("Có lỗi xảy ra: " + e.getMessage())
                            .build()
            );
        }
    }
}