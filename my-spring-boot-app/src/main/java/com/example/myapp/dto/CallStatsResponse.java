package com.example.myapp.dto;

import java.util.List;
import java.util.Map;

public class CallStatsResponse {

    private long totalCalls;
    private List<DimensionStat> byUserType;
    private List<DimensionStat> byUserLevel;
    private List<DimensionStat> byDepartment;
    private List<TrendPoint> trendByDay;

    public CallStatsResponse() {
    }

    public long getTotalCalls() {
        return totalCalls;
    }

    public void setTotalCalls(long totalCalls) {
        this.totalCalls = totalCalls;
    }

    public List<DimensionStat> getByUserType() {
        return byUserType;
    }

    public void setByUserType(List<DimensionStat> byUserType) {
        this.byUserType = byUserType;
    }

    public List<DimensionStat> getByUserLevel() {
        return byUserLevel;
    }

    public void setByUserLevel(List<DimensionStat> byUserLevel) {
        this.byUserLevel = byUserLevel;
    }

    public List<DimensionStat> getByDepartment() {
        return byDepartment;
    }

    public void setByDepartment(List<DimensionStat> byDepartment) {
        this.byDepartment = byDepartment;
    }

    public List<TrendPoint> getTrendByDay() {
        return trendByDay;
    }

    public void setTrendByDay(List<TrendPoint> trendByDay) {
        this.trendByDay = trendByDay;
    }

    public static class DimensionStat {
        private String dimension;
        private String value;
        private long count;

        public DimensionStat() {
        }

        public DimensionStat(String dimension, String value, long count) {
            this.dimension = dimension;
            this.value = value;
            this.count = count;
        }

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    public static class TrendPoint {
        private String date;
        private long count;

        public TrendPoint() {
        }

        public TrendPoint(String date, long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
