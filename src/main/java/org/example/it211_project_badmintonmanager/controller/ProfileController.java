package org.example.it211_project_badmintonmanager.controller;

import jakarta.validation.Valid;
import org.example.it211_project_badmintonmanager.dto.ChangePasswordDTO;
import org.example.it211_project_badmintonmanager.dto.ResponseDTO;
import org.example.it211_project_badmintonmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDTO<Void>> changePassword(@Valid @RequestBody ChangePasswordDTO request) {
        try {
            // Rút username từ Token hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            userService.changePassword(currentUsername, request);

            return ResponseEntity.ok(
                    ResponseDTO.<Void>builder().success(true).message("Đổi mật khẩu thành công!").build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Void>builder().success(false).message(e.getMessage()).build()
            );
        }
    }
}