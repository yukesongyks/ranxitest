package com.example.myapp.dto;

import java.util.List;

/**
 * 统计查询返回 VO。
 */
public class StatisticsVO {

    private String dimension;
    private List<StatisticsItem> items;

    public StatisticsVO() {
    }

    public StatisticsVO(String dimension, List<StatisticsItem> items) {
        this.dimension = dimension;
        this.items = items;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public List<StatisticsItem> getItems() {
        return items;
    }

    public void setItems(List<StatisticsItem> items) {
        this.items = items;
    }

    /**
     * 单个统计项。
     */
    public static class StatisticsItem {

        private String label;
        private Long count;
        private List<TrendPoint> trend;

        public StatisticsItem() {
        }

        public StatisticsItem(String label, Long count) {
            this.label = label;
            this.count = count;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public List<TrendPoint> getTrend() {
            return trend;
        }

        public void setTrend(List<TrendPoint> trend) {
            this.trend = trend;
        }
    }

    /**
     * 折线图趋势点。
     */
    public static class TrendPoint {

        private String date;
        private Long count;

        public TrendPoint() {
        }

        public TrendPoint(String date, Long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
