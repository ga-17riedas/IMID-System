package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.AnalyticsDataDTO;
import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.repository.DiagnosisReportRepository;
import com.example.medicalsystem.repository.UserRepository;
import com.example.medicalsystem.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public AnalyticsDataDTO getAnalyticsData(LocalDate startDate, LocalDate endDate) {
        AnalyticsDataDTO result = new AnalyticsDataDTO();
        
        // 添加调试日志
        System.out.println("AnalyticsServiceImpl.getAnalyticsData - 开始日期: " + startDate + ", 结束日期: " + endDate);
        
        // 获取当前时间段的诊断数据
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        System.out.println("查询时间范围: " + startDateTime + " 至 " + endDateTime);
        
        List<DiagnosisReport> currentPeriodDiagnoses = diagnosisReportRepository.findByCreatedAtBetween(
                startDateTime, endDateTime);
        
        System.out.println("查询到的诊断报告数量: " + currentPeriodDiagnoses.size());
        
        // 获取上一个时间段的诊断数据（用于计算趋势）
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate previousStartDate = startDate.minusDays(daysBetween);
        LocalDate previousEndDate = startDate.minusDays(1);
        List<DiagnosisReport> previousPeriodDiagnoses = diagnosisReportRepository.findByCreatedAtBetween(
                previousStartDate.atStartOfDay(), previousEndDate.plusDays(1).atStartOfDay());
        
        // 计算总诊断数和趋势
        int totalDiagnoses = currentPeriodDiagnoses.size();
        int previousTotalDiagnoses = previousPeriodDiagnoses.size();
        double diagnosesTrend = calculateTrend(totalDiagnoses, previousTotalDiagnoses);
        
        result.setTotalDiagnoses(totalDiagnoses);
        result.setDiagnosesTrend(diagnosesTrend);
        
        // 计算AI准确率和趋势
        double currentAccuracy = calculateAIAccuracy(currentPeriodDiagnoses);
        double previousAccuracy = calculateAIAccuracy(previousPeriodDiagnoses);
        double accuracyTrend = calculateTrend(currentAccuracy, previousAccuracy);
        
        result.setAiAccuracy(String.format("%.2f%%", currentAccuracy * 100));
        result.setAccuracyTrend(accuracyTrend);
        
        // 生成时间标签和数据
        generateTimeSeriesData(result, startDate, endDate, currentPeriodDiagnoses);
        
        // 计算准确率分布
        calculateAccuracyDistribution(result, currentPeriodDiagnoses);
        
        // 计算疾病类型分布
        calculateDiseaseDistribution(result, currentPeriodDiagnoses);
        
        return result;
    }
    
    private double calculateAIAccuracy(List<DiagnosisReport> diagnoses) {
        if (diagnoses.isEmpty()) {
            return 0.0;
        }
        
        long correctCount = diagnoses.stream()
                .filter(d -> d.getMedicalImage() != null)
                .filter(d -> d.getMedicalImage().getAiDiagnosis() != null && d.getDiagnosis() != null)
                .filter(d -> d.getMedicalImage().getAiDiagnosis().equals(d.getDiagnosis()))
                .count();
        
        return (double) correctCount / diagnoses.size();
    }
    
    private double calculateTrend(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        
        return ((current - previous) / previous) * 100.0;
    }
    
    private void generateTimeSeriesData(AnalyticsDataDTO result, LocalDate startDate, LocalDate endDate, 
                                       List<DiagnosisReport> diagnoses) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        // 如果时间跨度大于14天，按周聚合
        boolean groupByWeek = daysBetween > 14;
        
        if (groupByWeek) {
            // 按周聚合数据
            Map<String, List<DiagnosisReport>> weeklyDiagnoses = diagnoses.stream()
                    .collect(Collectors.groupingBy(d -> {
                        LocalDate date = d.getCreatedAt().toLocalDate();
                        int weekOfYear = date.get(java.time.temporal.WeekFields.of(Locale.getDefault()).weekOfYear());
                        return date.getYear() + "-W" + weekOfYear;
                    }));
            
            List<String> weeks = new ArrayList<>();
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                int weekOfYear = current.get(java.time.temporal.WeekFields.of(Locale.getDefault()).weekOfYear());
                String weekLabel = current.getYear() + "-W" + weekOfYear;
                if (!weeks.contains(weekLabel)) {
                    weeks.add(weekLabel);
                }
                current = current.plusDays(1);
            }
            
            List<Integer> totalData = weeks.stream()
                    .map(week -> weeklyDiagnoses.getOrDefault(week, Collections.emptyList()).size())
                    .collect(Collectors.toList());
            
            List<Integer> aiData = weeks.stream()
                    .map(week -> (int) weeklyDiagnoses.getOrDefault(week, Collections.emptyList()).stream()
                            .filter(d -> d.getMedicalImage() != null && d.getMedicalImage().getAiDiagnosis() != null)
                            .count())
                    .collect(Collectors.toList());
            
            result.setTimeLabels(weeks);
            result.setTotalDiagnosesData(totalData);
            result.setAiDiagnosesData(aiData);
            
        } else {
            // 按天聚合数据
            Map<String, List<DiagnosisReport>> dailyDiagnoses = diagnoses.stream()
                    .collect(Collectors.groupingBy(d -> 
                            d.getCreatedAt().toLocalDate().format(formatter)));
            
            List<String> days = IntStream.range(0, (int) daysBetween)
                    .mapToObj(i -> startDate.plusDays(i).format(formatter))
                    .collect(Collectors.toList());
            
            List<Integer> totalData = days.stream()
                    .map(day -> dailyDiagnoses.getOrDefault(day, Collections.emptyList()).size())
                    .collect(Collectors.toList());
            
            List<Integer> aiData = days.stream()
                    .map(day -> (int) dailyDiagnoses.getOrDefault(day, Collections.emptyList()).stream()
                            .filter(d -> d.getMedicalImage() != null && d.getMedicalImage().getAiDiagnosis() != null)
                            .count())
                    .collect(Collectors.toList());
            
            result.setTimeLabels(days);
            result.setTotalDiagnosesData(totalData);
            result.setAiDiagnosesData(aiData);
        }
    }
    
    private void calculateAccuracyDistribution(AnalyticsDataDTO result, List<DiagnosisReport> diagnoses) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("90-100", 0);
        distribution.put("80-90", 0);
        distribution.put("70-80", 0);
        distribution.put("60-70", 0);
        distribution.put("<60", 0);
        
        // 按医生分组计算每个医生的AI准确率
        Map<Long, List<DiagnosisReport>> diagnosisByDoctor = diagnoses.stream()
                .filter(d -> d.getDoctor() != null && d.getMedicalImage() != null && 
                       d.getMedicalImage().getAiDiagnosis() != null && d.getDiagnosis() != null)
                .collect(Collectors.groupingBy(d -> d.getDoctor().getId()));
        
        diagnosisByDoctor.forEach((doctorId, doctorDiagnoses) -> {
            double accuracy = calculateAIAccuracy(doctorDiagnoses) * 100;
            
            if (accuracy >= 90) {
                distribution.put("90-100", distribution.get("90-100") + 1);
            } else if (accuracy >= 80) {
                distribution.put("80-90", distribution.get("80-90") + 1);
            } else if (accuracy >= 70) {
                distribution.put("70-80", distribution.get("70-80") + 1);
            } else if (accuracy >= 60) {
                distribution.put("60-70", distribution.get("60-70") + 1);
            } else {
                distribution.put("<60", distribution.get("<60") + 1);
            }
        });
        
        result.setAccuracyDistribution(distribution);
    }
    
    private void calculateDiseaseDistribution(AnalyticsDataDTO result, List<DiagnosisReport> diagnoses) {
        Map<String, Integer> distribution = diagnoses.stream()
                .filter(d -> d.getDiagnosis() != null)
                .collect(Collectors.groupingBy(
                        DiagnosisReport::getDiagnosis,
                        Collectors.summingInt(d -> 1)
                ));
        
        result.setDiseaseDistribution(distribution);
    }
} 