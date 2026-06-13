package org.example.it211_project_badmintonmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "token_blacklist")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token; // Chứa chuỗi Access Token bị thu hồi

    @Column(name = "expiration_date")
    private Date expirationDate; // Lưu hạn sử dụng để sau này có thể dọn dẹp DB
}