package org.example.it211_project_badmintonmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.it211_project_badmintonmanager.dto.*;
import org.example.it211_project_badmintonmanager.security.JwtUtil;
import org.example.it211_project_badmintonmanager.service.TokenBlacklistService;
import org.example.it211_project_badmintonmanager.service.UserService;
import org.example.it211_project_badmintonmanager.entity.RefreshToken;
import org.example.it211_project_badmintonmanager.entity.User;
import org.example.it211_project_badmintonmanager.repository.UserRepository;
import org.example.it211_project_badmintonmanager.service.RefreshTokenService;
// Import thêm class CustomUserDetailsService (Đảm bảo đúng đường dẫn package security của bạn)
import org.example.it211_project_badmintonmanager.security.CustomUserDetailsService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder; // Import thêm thư viện này cho chức năng Logout
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/auth")
public class AuthController {
    // ✅ THÊM DÒNG NÀY VÀO THAY THẾ
    @Autowired
    private org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    // Bổ sung thêm bean này để dùng cho hàm refresh-token
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    // 1. ĐĂNG NHẬP
    // Thay đổi <?> thành <AuthResponseDTO>
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthResponseDTO>> login(@RequestBody AuthRequestDTO request) {
        try {
            org.springframework.security.authentication.AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = jwtUtil.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                    .accessToken(jwt)
                    .refreshToken(refreshToken.getToken())
                    .build();

            return ResponseEntity.ok(
                    // Thêm <AuthResponseDTO> vào trước builder()
                    ResponseDTO.<AuthResponseDTO>builder().success(true).message("Đăng nhập thành công").data(responseDTO).build()
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    // Thêm <AuthResponseDTO> vào trước builder()
                    ResponseDTO.<AuthResponseDTO>builder().success(false).message("Sai tài khoản hoặc mật khẩu").build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 2. ĐĂNG KÝ
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserDTO>> register(
            @Valid @RequestBody UserRegistrationDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            for (FieldError error : bindingResult.getFieldErrors()) {
                if (error.getDefaultMessage().contains("trống")) {
                    errorMessage = error.getDefaultMessage();
                    break;
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<UserDTO>builder().success(false).message(errorMessage).build()
            );
        }

        try {
            UserDTO createdUser = userService.registerCustomer(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ResponseDTO.<UserDTO>builder().success(true).message("Đăng ký thành công").data(createdUser).build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseDTO.<UserDTO>builder().success(false).message(e.getMessage()).build()
            );
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseDTO<AuthResponseDTO>> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO requestDTO) {
        String requestRefreshToken = requestDTO.getRefreshToken();

        try {
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        // Tạo Access Token mới tinh
                        String token = jwtUtil.generateToken(userDetailsService.loadUserByUsername(user.getUsername()));

                        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                                .accessToken(token)
                                .refreshToken(requestRefreshToken) // Giữ nguyên Refresh Token cũ cho khách
                                .build();

                        return ResponseEntity.ok(
                                ResponseDTO.<AuthResponseDTO>builder()
                                        .success(true)
                                        .message("Cấp lại Token thành công")
                                        .data(responseDTO)
                                        .build()
                        );
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh Token không tồn tại trong hệ thống hoặc đã hết hạn!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDTO.<AuthResponseDTO>builder().success(false).message(e.getMessage()).build()
            );
        }
    }
    // 3. ĐĂNG XUẤT (FR-03 - Revoke Token)
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout(HttpServletRequest request) { // 👉 THÊM HttpServletRequest VÀO ĐÂY

        // 1. Xóa Refresh Token trong Database (Luồng cũ của bạn)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                refreshTokenService.deleteByUserId(user.getId());
            }
        }

        // 2. Thu hồi Access Token (Lưu vào sổ đen TokenBlacklist) theo yêu cầu UC-03
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);
            try {
                // Rút xuất ngày hết hạn của token từ JwtUtil
                Date expirationDate = jwtUtil.extractExpiration(jwt);
                // Tống nó vào danh sách đen
                tokenBlacklistService.addToBlacklist(jwt, expirationDate);
            } catch (Exception e) {
                // Token có thể đã hết hạn sẵn, bỏ qua
            }
        }

        return ResponseEntity.ok(
                ResponseDTO.<Void>builder().success(true).message("Đăng xuất và thu hồi Token thành công").build()
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<String>> forgotPassword(@RequestBody ForgotPasswordDTO request) {
        try {
            // Lấy mật khẩu mới ngẫu nhiên từ Service
            String newPassword = userService.resetPassword(request.getEmail());

            return ResponseEntity.ok(
                    ResponseDTO.<String>builder()
                            .success(true)
                            // Trả về thẳng Postman để bạn dễ test. Sau này tích hợp gửi mail thì bỏ data đi nhé!
                            .message("Đã reset mật khẩu thành công!")
                            .data("Mật khẩu mới của bạn là: " + newPassword)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<String>builder().success(false).message(e.getMessage()).build()
            );
        }
    }
}