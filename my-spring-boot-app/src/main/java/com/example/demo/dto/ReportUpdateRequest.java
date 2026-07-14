package com.example.demo.dto;

public class ReportUpdateRequest {
    private String thisWeekWork;
    private String nextWeekPlan;
    private String status;
    
    public String getThisWeekWork() { return thisWeekWork; }
    public void setThisWeekWork(String thisWeekWork) { this.thisWeekWork = thisWeekWork; }
    
    public String getNextWeekPlan() { return nextWeekPlan; }
    public void setNextWeekPlan(String nextWeekPlan) { this.nextWeekPlan = nextWeekPlan; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}