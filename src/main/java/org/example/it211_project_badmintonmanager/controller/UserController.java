package org.example.it211_project_badmintonmanager.controller;

import jakarta.validation.Valid;
import org.example.it211_project_badmintonmanager.dto.ResponseDTO;
import org.example.it211_project_badmintonmanager.dto.UserDTO;
import org.example.it211_project_badmintonmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users") // Kéo URL dùng chung lên đây
public class UserController {

    @Autowired
    private UserService userService;

    // UC-05.1: Lấy danh sách phân trang
    @GetMapping
    public ResponseEntity<ResponseDTO<Page<UserDTO>>> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.searchUsers(keyword, pageable);

        return ResponseEntity.ok(
                ResponseDTO.<Page<UserDTO>>builder().success(true).message("Lấy danh sách thành công").data(users).build()
        );
    }

    // UC-05.2: Xem chi tiết 1 người dùng
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(
                    ResponseDTO.<UserDTO>builder().success(true).message("Lấy thông tin thành công").data(user).build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.<UserDTO>builder().success(false).message(e.getMessage()).build()
            );
        }
    }

    // UC-05.3: Cập nhật thông tin
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO updateDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Áp dụng "mẹo" ưu tiên lỗi có chữ "trống"
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
            UserDTO updatedUser = userService.updateUser(id, updateDTO);
            return ResponseEntity.ok(
                    ResponseDTO.<UserDTO>builder().success(true).message("Cập nhật thành công").data(updatedUser).build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<UserDTO>builder().success(false).message(e.getMessage()).build()
            );
        }
    }

    // UC-05.4: Xóa người dùng
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body(
                    ResponseDTO.<Void>builder().success(true).message("Xóa thành công").build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.<Void>builder().success(false).message(e.getMessage()).build()
            );
        }
    }
}