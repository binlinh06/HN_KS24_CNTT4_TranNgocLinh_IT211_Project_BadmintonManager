package org.example.it211_project_badmintonmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "court_name", nullable = false, length = 50)
    private String courtName;

    @Column(length = 50)
    private String type;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id")
    private BadmintonCluster cluster;
}
