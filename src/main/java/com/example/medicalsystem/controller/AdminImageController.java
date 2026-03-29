package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.dto.MedicalImageDetailDTO;
import com.example.medicalsystem.service.MedicalImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/images")
public class AdminImageController {

    @Autowired
    private MedicalImageService medicalImageService;

    @GetMapping
    public ResponseEntity<Page<MedicalImageDTO>> getImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String imageType) {
        
        PageRequest pageRequest = PageRequest.of(page, size, 
            Sort.by(Sort.Direction.DESC, "createdAt"));
            
        return ResponseEntity.ok(medicalImageService.getAdminImages(
            pageRequest, search, imageType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        medicalImageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<MedicalImageDetailDTO> getImageDetail(@PathVariable Long id) {
        return ResponseEntity.ok(medicalImageService.getImageDetail(id));
    }
} 