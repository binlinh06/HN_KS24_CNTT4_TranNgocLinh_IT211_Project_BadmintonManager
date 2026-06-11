package org.example.it211_project_badmintonmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserDTO {

    private Long id; // ID không cần validate vì hệ thống tự sinh hoặc dùng để định danh khi Update

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "Username phải dài từ 4 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng (Ví dụ: abc@domain.com)")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    // Các trường này có thể null nếu API update cho phép cập nhật từng phần
    private String role;
    private Boolean isEnabled;
}