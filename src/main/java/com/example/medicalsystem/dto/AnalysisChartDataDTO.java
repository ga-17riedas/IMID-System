package com.example.medicalsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnalysisChartDataDTO {
    private DiagnosisTrendData diagnosisTrend;
    private List<ChartDataItem> accuracyDistribution;
    private List<ChartDataItem> diseaseDistribution;
    private WorkloadData workload;

    @Data
    public static class DiagnosisTrendData {
        private List<String> labels;
        private List<Integer> totalDiagnosis;
        private List<Integer> aiDiagnosis;
    }

    @Data
    public static class WorkloadData {
        private List<String> doctorNames;
        private List<Integer> diagnosisCounts;
        private List<Double> accuracyRates;
    }

    @Data
    public static class ChartDataItem {
        private String name;
        private Integer value;
    }
} 