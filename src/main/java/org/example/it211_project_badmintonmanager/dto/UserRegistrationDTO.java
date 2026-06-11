package org.example.it211_project_badmintonmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "Username phải dài từ 4 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự để đảm bảo an toàn")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng (Ví dụ: abc@domain.com)")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    // Kiểm tra định dạng số điện thoại Việt Nam (bắt đầu bằng 0 hoặc +84, tiếp theo là các đầu số 3,5,7,8,9 và 8 số cuối)
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không hợp lệ (Ví dụ: 0987654321)")
    private String phoneNumber;
}