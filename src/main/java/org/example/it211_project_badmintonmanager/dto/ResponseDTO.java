package org.example.it211_project_badmintonmanager.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
}
