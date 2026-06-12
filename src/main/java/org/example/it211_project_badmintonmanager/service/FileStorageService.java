package org.example.it211_project_badmintonmanager.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileStorageService {

    @Autowired
    private Cloudinary cloudinary;

    public String storeFile(MultipartFile file) {
        try {
            // Validate định dạng và dung lượng (Có thể làm chi tiết hơn nếu muốn)
            if (file.isEmpty()) {
                throw new RuntimeException("Tệp tin không được để trống!");
            }

            // Gọi Cloudinary SDK để truyền File Stream lên Cloud
            // ObjectUtils.emptyMap() báo cho Cloudinary biết ta dùng các cấu hình mặc định
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // Sau khi upload thành công, Cloudinary trả về một map chứa nhiều thông tin.
            // Ta chỉ cần lấy cái link ảnh an toàn (secure_url)
            return uploadResult.get("secure_url").toString();

        } catch (IOException ex) {
            throw new RuntimeException("Lỗi khi kết nối với máy chủ Cloudinary. Vui lòng thử lại!", ex);
        }
    }
}