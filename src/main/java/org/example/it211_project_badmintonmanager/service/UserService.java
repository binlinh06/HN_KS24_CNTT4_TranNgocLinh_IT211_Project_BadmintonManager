package org.example.it211_project_badmintonmanager.service;

import org.example.it211_project_badmintonmanager.dto.UserDTO;
import org.example.it211_project_badmintonmanager.dto.UserRegistrationDTO;
import org.example.it211_project_badmintonmanager.entity.Role;
import org.example.it211_project_badmintonmanager.entity.User;
// Import 2 exception mới tạo
import org.example.it211_project_badmintonmanager.exception.DataConflictException;
import org.example.it211_project_badmintonmanager.exception.ResourceNotFoundException;
import org.example.it211_project_badmintonmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================
    // 1. CREATE (Tạo mới / Đăng ký)
    // ==========================================
    @Transactional
    public UserDTO registerCustomer(UserRegistrationDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername()) || userRepository.existsByEmail(dto.getEmail())) {
            // Thay đổi ở đây
            throw new DataConflictException("Username hoặc Email đã tồn tại");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.ROLE_CUSTOMER)
                .isEnabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    // ==========================================
    // 2. READ (Đọc dữ liệu)
    // ==========================================

    public Page<UserDTO> searchUsers(String keyword, Pageable pageable) {
        Page<User> userPage = userRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
        return userPage.map(this::mapToDTO);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                // Thay đổi ở đây
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        return mapToDTO(user);
    }

    // ==========================================
    // 3. UPDATE (Cập nhật thông tin)
    // ==========================================
    @Transactional
    public UserDTO updateUser(Long id, UserDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        // CHÈN THÊM DÒNG NÀY ĐỂ FIX LỖI RỖNG USERNAME TRÊN POSTMAN
        if (updateDTO.getUsername() != null) {
            user.setUsername(updateDTO.getUsername());
        }

        // Logic check trùng Email cực kỳ xuất sắc của bạn!
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new DataConflictException("Email này đã được sử dụng bởi tài khoản khác!");
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }

        // Ép kiểu chuỗi String từ DTO sang Enum Role cho Entity
        if (updateDTO.getRole() != null) {
            user.setRole(Role.valueOf(updateDTO.getRole()));
        }

        if (updateDTO.getIsEnabled() != null) {
            user.setIsEnabled(updateDTO.getIsEnabled());
        }

        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    // ==========================================
    // 4. DELETE (Xóa / Khóa tài khoản)
    // ==========================================
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                // Thay đổi ở đây
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        userRepository.delete(user);
    }

    // ==========================================
    // HÀM HỖ TRỢ (Mapper)
    // ==========================================
    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                // Sửa lỗi tại đây: Chuyển Enum Role thành chuỗi String để trả về cho Client
                .role(user.getRole().name())
                .isEnabled(user.getIsEnabled())
                .build();
    }
}