package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.ReportRequest;
import com.example.medicalsystem.model.*;
import com.example.medicalsystem.repository.*;
import com.example.medicalsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;

// iText 7 imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class MedicalReportServiceImpl implements MedicalReportService {

    private static final Logger log = LoggerFactory.getLogger(MedicalReportServiceImpl.class);

    @Autowired
    private MedicalImageService medicalImageService;

    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;
 
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalImageRepository medicalImageRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${report.font.path}")
    private String fontPath;  // 配置中文字体路径

    @Value("${report.retention.days:365}")
    private int retentionDays;

    @Value("${report.retention.auto-delete:false}")
    private boolean autoDelete;

    @Override
    @Transactional
    public DiagnosisReport saveReport(ReportRequest request, String doctorUsername) {
        try {
            // 1. 保存医学影像
            MedicalImage medicalImage = new MedicalImage();
            medicalImage.setPatientId(request.getPatientId());
            medicalImage.setDoctorId(getCurrentDoctor(doctorUsername).getId());
            medicalImage.setImageType(request.getImageType());
            medicalImage.setAiDiagnosis(request.getAiDiagnosis());
            medicalImage.setConfidenceScore(request.getConfidenceScore());

            // 处理图片
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                String imageUrl = imageStorageService.storeImage(request.getImage());
                medicalImage.setImageUrl(imageUrl);
                // 如果需要，设置 imagePath
                medicalImage.setImagePath(imageUrl);
            } else if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
                medicalImage.setImageUrl(request.getImageUrl());
                // 如果需要，设置 imagePath
                medicalImage.setImagePath(request.getImageUrl());
            } else {
                // 如果没有图片，设置默认值
                medicalImage.setImageUrl("/images/no-image.png");
                medicalImage.setImagePath("/images/no-image.png");
            }

            medicalImage = medicalImageRepository.save(medicalImage);

            // 2. 创建诊断报告
            DiagnosisReport report = new DiagnosisReport();
            report.setPatientId(request.getPatientId());
            report.setDoctorId(getCurrentDoctor(doctorUsername).getId());
            report.setDiagnosis(request.getDiagnosis());
            report.setTreatmentPlan(request.getTreatmentPlan());
            report.setRecommendations(request.getRecommendations());
            report.setMedicalImageId(medicalImage.getId());

            return diagnosisReportRepository.save(report);
        } catch (Exception e) {
            log.error("保存报告失败", e);
            throw new RuntimeException("保存报告失败: " + e.getMessage());
        }
    }

    private String determineImageType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return "OTHER";
        }
        filename = filename.toLowerCase();
        if (filename.contains("brain") || filename.contains("ct")) {
            return "BRAIN_TUMOR";
        } else if (filename.contains("covid") || filename.contains("chest")) {
            return "COVID";
        }
        return "OTHER";
    }

    private Doctor getCurrentDoctor(String username) {
        return doctorService.getDoctorByUsername(username);
    }

    @Override
    public Resource generatePdfReport(String username, ReportRequest request) {
        try {
            // 获取医生和患者信息
            Doctor doctor = doctorService.getDoctorByUsername(username);
            Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            // 创建临时文件
            File tempFile = File.createTempFile("medical_report_", ".pdf");
            PdfWriter writer = new PdfWriter(tempFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 设置中文字体
            PdfFont font = createChineseFont();
            
            // 添加标题
            Paragraph title = new Paragraph("医学影像分析报告")
                .setFont(font)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // 添加基本信息表格
            Table infoTable = new Table(new float[]{1, 3})
                .setWidth(UnitValue.createPercentValue(100));

            // 添加患者信息
            addTableRow(infoTable, "患者姓名:", patient.getFullName(), font);
            addTableRow(infoTable, "性别:", patient.getGender(), font);
            addTableRow(infoTable, "年龄:", patient.getAge().toString(), font);
            
            // 添加医生信息
            addTableRow(infoTable, "主诊医师:", doctor.getFullName(), font);
            addTableRow(infoTable, "所属医院:", doctor.getHospital(), font);
            addTableRow(infoTable, "科室:", doctor.getDepartment(), font);
            
            document.add(infoTable);

            // 添加诊断结果
            document.add(new Paragraph("诊断结果")
                .setFont(font)
                .setFontSize(14)
                .setBold());
            document.add(new Paragraph(request.getDiagnosis())
                .setFont(font)
                .setFontSize(12));

            // 添加治疗方案
            document.add(new Paragraph("治疗方案")
                .setFont(font)
                .setFontSize(14)
                .setBold());
            document.add(new Paragraph(request.getTreatmentPlan())
                .setFont(font)
                .setFontSize(12));

            // 添加医生建议
            document.add(new Paragraph("医生建议")
                .setFont(font)
                .setFontSize(14)
                .setBold());
            document.add(new Paragraph(request.getRecommendations())
                .setFont(font)
                .setFontSize(12));

            // 添加图片
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                MedicalImage medicalImage = medicalImageService.saveImage(
                    request.getImage(),
                    request.getPatientId(),
                    doctor.getId()
                );
                String imageUrl = medicalImage.getImageUrl();
                if (imageUrl != null) {
                    if (imageUrl.startsWith("/")) {
                        imageUrl = baseUrl + imageUrl;
                    }
                    Image image = new Image(ImageDataFactory.create(new URL(imageUrl)))
                        .setWidth(400)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER);
                    document.add(image);
                }
            }

            // 添加签名和日期
            document.add(new Paragraph(String.format(
                "医师签名: %s\n报告日期: %s",
                doctor.getFullName(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ))
            .setFont(font)
            .setTextAlignment(TextAlignment.RIGHT));

            // 关闭文档
            document.close();

            // 返回生成的PDF文件作为Resource
            return new FileSystemResource(tempFile);
        } catch (Exception e) {
            throw new RuntimeException("生成PDF报告失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<DiagnosisReport> getReportsByDoctor(String username, int page, int size) {
        Doctor doctor = doctorService.getDoctorByUsername(username);
        if (doctor == null) {
            throw new RuntimeException("医生不存在");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return diagnosisReportRepository.findByDoctorId(doctor.getId(), pageable);
    }

    @Override
    public Resource generatePdfReportById(Long reportId, String username) {
        DiagnosisReport report = diagnosisReportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
            
        // 如果提供了用户名，验证是否为医生并检查权限
        if (username != null) {
            try {
                Doctor doctor = doctorService.getDoctorByUsername(username);
                // 验证报告是否属于该医生
                if (!report.getDoctor().getId().equals(doctor.getId())) {
                    throw new RuntimeException("Unauthorized access to report");
                }
            } catch (Exception e) {
                // 如果不是医生，则忽略权限检查（患者端已在Controller中验证权限）
                log.info("非医生用户请求PDF: {}", username);
            }
        }

        try {
            // 创建临时文件
            File tempFile = File.createTempFile("report_", ".pdf");
            PdfWriter writer = new PdfWriter(tempFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 使用报告数据生成PDF
            generatePdfContent(document, report);

            document.close();
            return new FileSystemResource(tempFile);
        } catch (Exception e) {
            throw new RuntimeException("生成PDF报告失败: " + e.getMessage(), e);
        }
    }

    private void generatePdfContent(Document document, DiagnosisReport report) throws IOException {
        // 创建中文字体
        PdfFont chineseFont = createChineseFont();
        
        // 设置文档默认字体
        document.setFont(chineseFont);
        
        // 添加标题
        document.add(new Paragraph("医学影像诊断报告")
            .setFont(chineseFont)
            .setFontSize(20)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold());

        // 添加基本信息表格
        Table infoTable = new Table(new float[]{1, 2, 1, 2})
            .setWidth(UnitValue.createPercentValue(100));

        // 添加患者信息
        addInfoCell(infoTable, "姓    名：", report.getPatient().getFullName(), chineseFont);
        addInfoCell(infoTable, "性    别：", report.getPatient().getGender(), chineseFont);
        addInfoCell(infoTable, "年    龄：", report.getPatient().getAge() + "岁", chineseFont);
        
        addInfoCell(infoTable, "检查号：", "CT-" + report.getId(), chineseFont);
        addInfoCell(infoTable, "门诊号：", "IA" + report.getPatient().getId(), chineseFont);
        addInfoCell(infoTable, "病区床号：", "", chineseFont);
        
        addInfoCell(infoTable, "检查部位：", "胸部CT平扫、全腹部CT平扫（包括上下腹部）", chineseFont, 4);
        addInfoCell(infoTable, "检查时间：", report.getCreatedAt().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), chineseFont, 4);
        
        document.add(infoTable);

        // 添加分隔线
        document.add(new Paragraph("").setHeight(20));
        ILineDrawer line = new SolidLine(1f);
        document.add(new LineSeparator(line).setMarginBottom(20));

        // 添加图片
        float[] imageWidths = {150f, 150f, 150f, 150f};
        Table imageTable = new Table(UnitValue.createPercentArray(imageWidths))
            .setWidth(UnitValue.createPercentValue(100))
            .setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        if (report.getMedicalImage() != null && report.getMedicalImage().getImageUrl() != null) {
            String imageUrl = report.getMedicalImage().getImageUrl();
            if (imageUrl.startsWith("/")) {
                imageUrl = baseUrl + imageUrl;
            }
            try {
                Image image = new Image(ImageDataFactory.create(new URL(imageUrl)))
                    .setWidth(140)
                    .setHeight(140)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
                imageTable.addCell(new Cell().add(image).setBorder(null));
            } catch (Exception e) {
                log.error("Failed to add image to PDF: {}", e.getMessage());
                // 添加一个占位符或提示
                Cell errorCell = new Cell()
                    .add(new Paragraph("图片加载失败").setFontSize(12).setFont(chineseFont))
                    .setHeight(140)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
                imageTable.addCell(errorCell);
            }
        } else {
            // 如果没有图片，添加一个提示
            Cell noImageCell = new Cell()
                .add(new Paragraph("暂无医学影像").setFontSize(12).setFont(chineseFont))
                .setHeight(140)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
            imageTable.addCell(noImageCell);
        }
        document.add(imageTable);

        // 添加分隔线
        document.add(new Paragraph("").setHeight(20));
        document.add(new LineSeparator(line).setMarginBottom(20));

        // CT 所见
        document.add(new Paragraph("CT 所见")
            .setFont(chineseFont)
            .setFontSize(14)
            .setBold());
        document.add(new Paragraph("").setHeight(10));
        document.add(new Paragraph(report.getDiagnosis())
            .setFont(chineseFont)
            .setFontSize(12)
            .setMultipliedLeading(1.5f));

        document.add(new Paragraph("").setHeight(20));

        // CT 诊断意见
        document.add(new Paragraph("CT 诊断意见")
            .setFont(chineseFont)
            .setFontSize(14)
            .setBold());
        document.add(new Paragraph("").setHeight(10));
        document.add(new Paragraph(report.getTreatmentPlan())
            .setFont(chineseFont)
            .setFontSize(12)
            .setMultipliedLeading(1.5f));

        document.add(new Paragraph("").setHeight(30));

        // 添加底部信息
        Table footerTable = new Table(new float[]{1, 1, 2})
            .setWidth(UnitValue.createPercentValue(100));
        
        addFooterCell(footerTable, "审核医师：", report.getDoctor().getFullName(), chineseFont);
        addFooterCell(footerTable, "报告医师：", report.getDoctor().getFullName(), chineseFont);
        addFooterCell(footerTable, "审核时间：", 
            report.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), chineseFont);
            
        document.add(footerTable);
        
        document.add(new Paragraph("").setHeight(20));
        document.add(new LineSeparator(line).setMarginBottom(20));

        // 添加注意事项
        document.add(new Paragraph("注意事项：请妥善保管此报告单及二维码，扫描二维码可查看影像检查信息。")
            .setFont(chineseFont)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setItalic());
            
        document.add(new Paragraph("*******此报告仅供本院医生参考，不作证明用*******")
            .setFont(chineseFont)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setItalic());
    }

    private PdfFont createChineseFont() throws IOException {
        log.info("Attempting to load font from path: {}", fontPath);
        
        try {
            // 检查字体文件是否存在
            File fontFile = new File(fontPath);
            if (!fontFile.exists()) {
                log.error("Font file not found at path: {}", fontPath);
                throw new IOException("Font file not found");
            }
            
            // 使用 IDENTITY_H 编码
            return PdfFontFactory.createFont(
                fontPath,
                PdfEncodings.IDENTITY_H,  // 改用 IDENTITY_H 编码
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
            );
        } catch (IOException e) {
            log.error("Failed to load primary font: {}", e.getMessage());
            
            // 尝试加载备用字体
            try {
                log.info("Attempting to load fallback font SimSun");
                return PdfFontFactory.createFont(
                    "C:/Windows/Fonts/simsun.ttc,0",
                    PdfEncodings.IDENTITY_H,  // 改用 IDENTITY_H 编码
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                );
            } catch (IOException e2) {
                log.error("Failed to load fallback font: {}", e2.getMessage());
                
                // 最后尝试使用 iText 内置的中文字体
                try {
                    log.info("Attempting to load built-in font");
                    return PdfFontFactory.createFont(
                        StandardFonts.COURIER,  // 使用基本字体
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                    );
                } catch (IOException e3) {
                    log.error("Failed to load built-in font: {}", e3.getMessage());
                    // 如果所有尝试都失败，使用默认字体
                    log.warn("Falling back to default Helvetica font");
                    return PdfFontFactory.createFont(StandardFonts.HELVETICA);
                }
            }
        }
    }

    private void addInfoCell(Table table, String label, String value, PdfFont font) {
        addInfoCell(table, label, value, font, 1);
    }

    private void addInfoCell(Table table, String label, String value, PdfFont font, int colspan) {
        table.addCell(
            new Cell().add(new Paragraph(label).setFont(font))
                .setFontSize(12)
                .setBorder(null)
        );
        table.addCell(
            new Cell(1, colspan).add(new Paragraph(value).setFont(font))
                .setFontSize(12)
                .setBorder(null)
        );
    }

    private void addFooterCell(Table table, String label, String value, PdfFont font) {
        Cell cell = new Cell().add(
            new Paragraph(label + value)
                .setFont(font)
                .setFontSize(12)
        );
        table.addCell(cell.setBorder(null));
    }

    private void addTableRow(Table table, String label, String value, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(label).setFont(font).setBold()));
        table.addCell(new Cell().add(new Paragraph(value).setFont(font)));
    }

    // 定时任务检查过期报告
    @Scheduled(cron = "0 0 1 * * ?")  // 每天凌晨1点执行
    @Transactional
    public void checkExpiredReports() {
        LocalDateTime now = LocalDateTime.now();
        List<DiagnosisReport> expiredReports = diagnosisReportRepository
            .findByExpireDateBeforeAndExpiredFalse(now);

        for (DiagnosisReport report : expiredReports) {
            if (autoDelete) {
                diagnosisReportRepository.delete(report);
            } else {
                report.setExpired(true);
                diagnosisReportRepository.save(report);
            }
        }
    }

    @Override
    public DiagnosisReport getReportById(Long id) {
        return diagnosisReportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }
} 