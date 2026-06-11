package org.example.it211_project_badmintonmanager.service;

import jakarta.transaction.Transactional;
import org.example.it211_project_badmintonmanager.entity.Court;
import org.example.it211_project_badmintonmanager.entity.CourtImage;
import org.example.it211_project_badmintonmanager.repository.CourtImageRepository;
import org.example.it211_project_badmintonmanager.repository.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourtService {
    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private CourtImageRepository courtImageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public List<String> uploadCourtImages(Long courtId, List<MultipartFile> files) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân với ID: " + courtId));

        List<String> savedImageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // 1. Lưu file vật lý vào thư mục
                String fileName = fileStorageService.storeFile(file);

                // 2. Tạo record lưu vào Database
                CourtImage courtImage = CourtImage.builder()
                        .imageUrl(fileName)
                        .court(court)
                        .build();
                courtImageRepository.save(courtImage);

                savedImageUrls.add(fileName);
            }
        }
        return savedImageUrls; // Trả về danh sách tên file vừa lưu
    }
}
