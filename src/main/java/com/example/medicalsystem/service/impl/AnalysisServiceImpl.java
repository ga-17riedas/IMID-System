package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.AnalysisStatisticsDTO;
import com.example.medicalsystem.dto.AnalysisChartDataDTO;
import com.example.medicalsystem.repository.*;
import com.example.medicalsystem.service.AnalysisService;
import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;
    
    @Autowired
    private MedicalImageRepository medicalImageRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public AnalysisStatisticsDTO getStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        AnalysisStatisticsDTO stats = new AnalysisStatisticsDTO();
        
        // 获取总诊断数和趋势
        Integer currentPeriodDiagnosis = diagnosisReportRepository.countByCreatedAtBetween(startDate, endDate);
        Integer previousPeriodDiagnosis = diagnosisReportRepository.countByCreatedAtBetween(
            startDate.minus(30, ChronoUnit.DAYS), 
            endDate.minus(30, ChronoUnit.DAYS)
        );
        
        stats.setTotalDiagnosis(currentPeriodDiagnosis);
        stats.setDiagnosisTrend(calculateTrend(previousPeriodDiagnosis, currentPeriodDiagnosis));

        // 获取AI诊断准确率和趋势
        Double currentAccuracy = medicalImageRepository.calculateAiAccuracy(startDate, endDate);
        Double previousAccuracy = medicalImageRepository.calculateAiAccuracy(
            startDate.minus(30, ChronoUnit.DAYS),
            endDate.minus(30, ChronoUnit.DAYS)
        );
        
        stats.setAiAccuracy(currentAccuracy);
        stats.setAiAccuracyTrend(calculateTrend(previousAccuracy, currentAccuracy));

        return stats;
    }

    @Override
    public AnalysisChartDataDTO getChartData(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        AnalysisChartDataDTO chartData = new AnalysisChartDataDTO();
        
        // 设置诊断趋势数据
        AnalysisChartDataDTO.DiagnosisTrendData trendData = generateDiagnosisTrendData(startDate, endDate);
        chartData.setDiagnosisTrend(trendData);
        
        // 设置AI准确率分布
        List<AnalysisChartDataDTO.ChartDataItem> accuracyItems = generateAccuracyDistribution(startDate, endDate);
        chartData.setAccuracyDistribution(accuracyItems);
        
        // 设置疾病类型分布
        List<AnalysisChartDataDTO.ChartDataItem> diseaseItems = generateDiseaseDistribution(startDate, endDate);
        chartData.setDiseaseDistribution(diseaseItems);
        
        // 设置医生工作量数据
        AnalysisChartDataDTO.WorkloadData workloadData = generateDoctorWorkloadData(startDate, endDate);
        chartData.setWorkload(workloadData);

        return chartData;
    }
    
    /**
     * 生成诊断趋势数据
     */
    private AnalysisChartDataDTO.DiagnosisTrendData generateDiagnosisTrendData(LocalDateTime startDate, LocalDateTime endDate) {
        AnalysisChartDataDTO.DiagnosisTrendData trendData = new AnalysisChartDataDTO.DiagnosisTrendData();
        
        // 计算日期范围内的天数
        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        
        // 确定数据粒度 - 如果超过14天则按周聚合，否则按天聚合
        boolean groupByWeek = daysBetween > 14;
        
        List<String> labels = new ArrayList<>();
        List<Integer> totalValues = new ArrayList<>();
        List<Integer> aiValues = new ArrayList<>();
        
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("'W'w, yyyy");
        
        if (groupByWeek) {
            // 按周聚合数据
            Map<String, List<DiagnosisReport>> weeklyReports = diagnosisReportRepository
                .findByCreatedAtBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(report -> {
                    return report.getCreatedAt().format(weekFormatter);
                }));
            
            // 生成所有周的标签
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                String weekLabel = current.format(weekFormatter);
                if (!labels.contains(weekLabel)) {
                    labels.add(weekLabel);
                    
                    List<DiagnosisReport> reports = weeklyReports.getOrDefault(weekLabel, Collections.emptyList());
                    totalValues.add(reports.size());
                    
                    // 计算AI诊断的数量
                    long aiCount = reports.stream()
                        .filter(r -> r.getMedicalImage() != null && r.getMedicalImage().getAiDiagnosis() != null)
                        .count();
                    aiValues.add((int) aiCount);
                }
                current = current.plusDays(7);
            }
        } else {
            // 按天聚合数据
            Map<String, List<DiagnosisReport>> dailyReports = diagnosisReportRepository
                .findByCreatedAtBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(report -> {
                    return report.getCreatedAt().format(dayFormatter);
                }));
            
            // 生成所有天的标签
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                String dayLabel = current.format(dayFormatter);
                labels.add(dayLabel);
                
                List<DiagnosisReport> reports = dailyReports.getOrDefault(dayLabel, Collections.emptyList());
                totalValues.add(reports.size());
                
                // 计算AI诊断的数量
                long aiCount = reports.stream()
                    .filter(r -> r.getMedicalImage() != null && r.getMedicalImage().getAiDiagnosis() != null)
                    .count();
                aiValues.add((int) aiCount);
                
                current = current.plusDays(1);
            }
        }
        
        trendData.setLabels(labels);
        trendData.setTotalDiagnosis(totalValues);
        trendData.setAiDiagnosis(aiValues);
        
        return trendData;
    }
    
    /**
     * 生成AI准确率分布数据
     */
    private List<AnalysisChartDataDTO.ChartDataItem> generateAccuracyDistribution(LocalDateTime startDate, LocalDateTime endDate) {
        List<AnalysisChartDataDTO.ChartDataItem> items = new ArrayList<>();
        
        // 获取所有医学图像
        List<Object[]> accuracyData = medicalImageRepository.getAiAccuracyDistribution(startDate, endDate);
        
        // 准确率区间
        String[] ranges = {"90-100%", "80-90%", "70-80%", "60-70%", "< 60%"};
        int[] counts = new int[5];
        
        // 计算每个区间的图像数量
        for (Object[] data : accuracyData) {
            if (data.length >= 2) {
                Double accuracy = (Double) data[0];
                Long count = (Long) data[1];
                
                if (accuracy >= 0.9) {
                    counts[0] += count.intValue();
                } else if (accuracy >= 0.8) {
                    counts[1] += count.intValue();
                } else if (accuracy >= 0.7) {
                    counts[2] += count.intValue();
                } else if (accuracy >= 0.6) {
                    counts[3] += count.intValue();
                } else {
                    counts[4] += count.intValue();
                }
            }
        }
        
        // 创建图表数据项
        for (int i = 0; i < ranges.length; i++) {
            AnalysisChartDataDTO.ChartDataItem item = new AnalysisChartDataDTO.ChartDataItem();
            item.setName(ranges[i]);
            item.setValue(counts[i]);
            items.add(item);
        }
        
        return items;
    }
    
    /**
     * 生成疾病类型分布数据
     */
    private List<AnalysisChartDataDTO.ChartDataItem> generateDiseaseDistribution(LocalDateTime startDate, LocalDateTime endDate) {
        List<AnalysisChartDataDTO.ChartDataItem> items = new ArrayList<>();
        
        // 获取诊断报告中的疾病类型分布
        List<Object[]> diseaseData = diagnosisReportRepository.getDiseaseDistribution(startDate, endDate);
        
        // 转换为图表数据项
        for (Object[] data : diseaseData) {
            if (data.length >= 2 && data[0] != null) {
                String diseaseName = (String) data[0];
                Long count = (Long) data[1];
                
                AnalysisChartDataDTO.ChartDataItem item = new AnalysisChartDataDTO.ChartDataItem();
                item.setName(diseaseName);
                item.setValue(count.intValue());
                items.add(item);
            }
        }
        
        // 如果疾病类型太多，只保留前10种，其余归为"其他"
        if (items.size() > 10) {
            // 按数量排序
            items.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
            
            // 取前9种疾病
            List<AnalysisChartDataDTO.ChartDataItem> topItems = items.subList(0, 9);
            
            // 计算其他疾病的总数
            int othersValue = items.subList(9, items.size()).stream()
                .mapToInt(AnalysisChartDataDTO.ChartDataItem::getValue)
                .sum();
            
            // 创建"其他"项
            AnalysisChartDataDTO.ChartDataItem othersItem = new AnalysisChartDataDTO.ChartDataItem();
            othersItem.setName("其他");
            othersItem.setValue(othersValue);
            
            // 创建新的列表
            List<AnalysisChartDataDTO.ChartDataItem> result = new ArrayList<>(topItems);
            result.add(othersItem);
            
            return result;
        }
        
        return items;
    }
    
    /**
     * 生成医生工作量数据
     */
    private AnalysisChartDataDTO.WorkloadData generateDoctorWorkloadData(LocalDateTime startDate, LocalDateTime endDate) {
        AnalysisChartDataDTO.WorkloadData workloadData = new AnalysisChartDataDTO.WorkloadData();
        
        // 获取所有医生
        List<Doctor> doctors = doctorRepository.findAll();
        
        List<String> doctorNames = new ArrayList<>();
        List<Integer> diagnosisCounts = new ArrayList<>();
        List<Double> accuracyRates = new ArrayList<>();
        
        // 计算每个医生的诊断数量和准确率
        for (Doctor doctor : doctors) {
            // 获取医生的诊断报告数量
            int diagnosisCount = diagnosisReportRepository.countByDoctorIdAndCreatedAtBetween(
                doctor.getId(), startDate, endDate);
            
            // 获取医生的AI诊断准确率
            Double accuracy = medicalImageRepository.calculateDoctorAiAccuracy(
                doctor.getId(), startDate, endDate);
            
            // 只添加有诊断记录的医生
            if (diagnosisCount > 0) {
                doctorNames.add(doctor.getFullName());
                diagnosisCounts.add(diagnosisCount);
                accuracyRates.add(accuracy != null ? accuracy * 100 : 0.0);
            }
        }
        
        // 如果医生数量太多，只保留前10名
        if (doctorNames.size() > 10) {
            // 创建医生索引列表
            List<Integer> indices = IntStream.range(0, doctorNames.size()).boxed().collect(Collectors.toList());
            
            // 创建一个final的引用，用于lambda表达式
            final List<Integer> finalDiagnosisCounts = diagnosisCounts;
            // 按诊断数量排序
            indices.sort((a, b) -> Integer.compare(finalDiagnosisCounts.get(b), finalDiagnosisCounts.get(a)));
            
            // 取前10名医生
            List<String> topDoctorNames = new ArrayList<>();
            List<Integer> topDiagnosisCounts = new ArrayList<>();
            List<Double> topAccuracyRates = new ArrayList<>();
            
            for (int i = 0; i < Math.min(10, indices.size()); i++) {
                int index = indices.get(i);
                topDoctorNames.add(doctorNames.get(index));
                topDiagnosisCounts.add(diagnosisCounts.get(index));
                topAccuracyRates.add(accuracyRates.get(index));
            }
            
            doctorNames = topDoctorNames;
            diagnosisCounts = topDiagnosisCounts;
            accuracyRates = topAccuracyRates;
        }
        
        workloadData.setDoctorNames(doctorNames);
        workloadData.setDiagnosisCounts(diagnosisCounts);
        workloadData.setAccuracyRates(accuracyRates);
        
        return workloadData;
    }

    private Double calculateTrend(Number previous, Number current) {
        if (previous == null || previous.doubleValue() == 0) {
            return 0.0;
        }
        return ((current.doubleValue() - previous.doubleValue()) / previous.doubleValue()) * 100;
    }
} 