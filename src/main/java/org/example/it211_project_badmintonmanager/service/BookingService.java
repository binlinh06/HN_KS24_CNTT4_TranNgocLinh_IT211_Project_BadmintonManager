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
    public void createBooking(BookingRequestDTO dto, String currentUsername) {
        // 1. Kiểm tra Court tồn tại
        Court court = courtRepository.findById(dto.getCourtId())
                // Thay thế bằng ResourceNotFoundException
                .orElseThrow(() -> new ResourceNotFoundException("Sân cầu lông không tồn tại"));

        // 2. Lấy thông tin user đang thao tác
        User user = userRepository.findByUsername(currentUsername)
                // Thay thế bằng ResourceNotFoundException
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        // 3. Validate trùng lịch đặt (Validation)
        boolean isConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlot(
                dto.getCourtId(), dto.getBookingDate(), dto.getTimeSlot()
        );
        if (isConflict) {
            // Thay thế bằng DataConflictException
            throw new DataConflictException("Khung giờ này đã có người đặt!");
        }

        // 4. Tạo bản ghi Booking trạng thái PENDING
        Booking booking = Booking.builder()
                .court(court)
                .user(user)
                .bookingDate(dto.getBookingDate())
                .timeSlot(dto.getTimeSlot())
                .status(BookingStatus.PENDING)
                .build();

        bookingRepository.save(booking);
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
                .status(booking.getStatus() != null ? booking.getStatus().name() : "PENDING")
                .build();
    }
}