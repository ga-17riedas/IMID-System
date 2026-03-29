package com.example.medicalsystem.service;

import com.example.medicalsystem.model.MedicalImage;
import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.dto.MedicalImageDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface MedicalImageService {
    MedicalImage saveImage(MultipartFile file, Long patientId, Long doctorId) throws IOException;
    Page<MedicalImageDTO> getAdminImages(Pageable pageable, String search, String imageType);
    void deleteImage(Long id);
    MedicalImageDetailDTO getImageDetail(Long id);
} 