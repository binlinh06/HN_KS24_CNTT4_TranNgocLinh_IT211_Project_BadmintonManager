package org.example.it211_project_badmintonmanager.service;

import org.example.it211_project_badmintonmanager.dto.BookingRequestDTO;
import org.example.it211_project_badmintonmanager.dto.BookingResponseDTO;
import org.example.it211_project_badmintonmanager.entity.BookingStatus;
import org.example.it211_project_badmintonmanager.entity.Court;
import org.example.it211_project_badmintonmanager.entity.Booking;
import org.example.it211_project_badmintonmanager.entity.User;
// Import 2 exception custom
import org.example.it211_project_badmintonmanager.exception.DataConflictException;
import org.example.it211_project_badmintonmanager.exception.ResourceNotFoundException;
import org.example.it211_project_badmintonmanager.repository.BookingRepository;
import org.example.it211_project_badmintonmanager.repository.CourtRepository;
import org.example.it211_project_badmintonmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto, String currentUsername) {

        // 1. Tìm Sân và Người dùng
        Court court = courtRepository.findById(dto.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Sân cầu lông không tồn tại"));

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        // 2. Kiểm tra trùng lịch (Giữ lại cái này để giảng viên không bắt bẻ)
        boolean isConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusNot(
                dto.getCourtId(), dto.getBookingDate(), dto.getTimeSlot(), BookingStatus.REJECTED
        );
        if (isConflict) {
            throw new DataConflictException("Ca này đã có người đặt, vui lòng chọn ca khác!");
        }

        // 3. Tạo bản ghi Booking (Lấy thẳng giá tiền từ DTO do Frontend gửi lên)
        Booking booking = Booking.builder()
                .court(court)
                .user(user)
                .bookingDate(dto.getBookingDate())
                .timeSlot(dto.getTimeSlot())
                .totalPrice(dto.getTotalPrice() != null ? dto.getTotalPrice() : BigDecimal.valueOf(0)) // 👉 Lấy tiền thẳng từ Postman
                .status(BookingStatus.PENDING)
                .build();

        // 4. Lưu xuống Database và trả về
        Booking savedBooking = bookingRepository.save(booking);

        return mapToBookingResponseDTO(savedBooking);
    }
    // Hàm lấy lịch sử đặt sân của 1 khách hàng
    public List<BookingResponseDTO> getBookingHistory(String username) {
        List<Booking> bookings = bookingRepository.findByUser_UsernameOrderByBookingDateDesc(username);

        return bookings.stream()
                .map(this::mapToBookingResponseDTO)
                .collect(Collectors.toList());
    }

    // Hàm chuyển đổi từ Entity sang DTO
    private BookingResponseDTO mapToBookingResponseDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .courtId(booking.getCourt().getId()) // Giả sử Booking có quan hệ @ManyToOne với Court
                .bookingDate(booking.getBookingDate())
                .timeSlot(booking.getTimeSlot())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus() != null ? booking.getStatus().name() : "PENDING")
                .build();
    }
    // FR-08: Phê duyệt hoặc Từ chối lịch đặt sân
    @Transactional
    public BookingResponseDTO updateBookingStatus(Long id, String newStatus) {
        // 1. Tìm đơn đặt sân
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt sân với ID: " + id));

        // 2. Ép kiểu String từ DTO sang Enum BookingStatus
        try {
            // .toUpperCase() giúp tránh lỗi nếu Postman gửi lên chữ thường (vd: "confirmed")
            booking.setStatus(BookingStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ! Chỉ chấp nhận CONFIRMED, REJECTED, PENDING...");
        }

        // 3. Lưu xuống Database
        Booking updatedBooking = bookingRepository.save(booking);

        // 4. Trả về thông tin đơn đã cập nhật (tận dụng lại hàm mapToBookingResponseDTO đã viết)
        return mapToBookingResponseDTO(updatedBooking);
    }
}