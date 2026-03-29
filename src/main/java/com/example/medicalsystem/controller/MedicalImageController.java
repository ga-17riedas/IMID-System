package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.service.MedicalImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical")
public class MedicalImageController {

    private static final Logger log = LoggerFactory.getLogger(MedicalImageController.class);

    @Autowired
    private MedicalImageService medicalImageService;

    @GetMapping("/admin/images")
    public ResponseEntity<Page<MedicalImageDTO>> getAdminImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String imageType) {
        
        PageRequest pageRequest = PageRequest.of(page, size, 
            Sort.by(Sort.Direction.DESC, "createdAt"));
            
        // 添加调试日志    
        log.debug("Fetching images with params: page={}, size={}, search={}, imageType={}", 
            page, size, search, imageType);
        
        Page<MedicalImageDTO> result = medicalImageService.getAdminImages(pageRequest, search, imageType);
        
        // 添加结果日志
        log.debug("Found {} images", result.getTotalElements());
        
        return ResponseEntity.ok(result);
    }
} 