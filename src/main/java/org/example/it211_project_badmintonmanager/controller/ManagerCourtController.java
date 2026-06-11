package org.example.it211_project_badmintonmanager.controller;

import org.example.it211_project_badmintonmanager.dto.ResponseDTO;
import org.example.it211_project_badmintonmanager.service.CourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/courts")
public class ManagerCourtController {

    @Autowired
    private CourtService courtService;

    // UC-09: Upload nhiều ảnh cho 1 sân
    @PostMapping(value = "/{courtId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<List<String>>> uploadImages(
            @PathVariable Long courtId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> savedFiles = courtService.uploadCourtImages(courtId, files);

            return ResponseEntity.ok(
                    ResponseDTO.<List<String>>builder()
                            .success(true)
                            .message("Upload " + savedFiles.size() + " ảnh thành công!")
                            .data(savedFiles)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<List<String>>builder()
                            .success(false)
                            .message("Lỗi upload: " + e.getMessage())
                            .build()
            );
        }
    }
}