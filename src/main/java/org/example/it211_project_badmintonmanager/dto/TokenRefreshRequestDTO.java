package org.example.it211_project_badmintonmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequestDTO {
    @NotBlank(message = "Refresh Token không được để trống")
    private String refreshToken;
}