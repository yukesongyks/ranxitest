package com.example.demo.dto;

import com.example.demo.entity.ReportStatus;

public class ReportResponse {
    private Long id;
    private Long authorId;
    private String authorName;
    private String thisWeekWork;
    private String nextWeekPlan;
    private ReportStatus status;
    private String rejectReason;
    private String createdAt;
    private String updatedAt;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public String getThisWeekWork() { return thisWeekWork; }
    public void setThisWeekWork(String thisWeekWork) { this.thisWeekWork = thisWeekWork; }
    
    public String getNextWeekPlan() { return nextWeekPlan; }
    public void setNextWeekPlan(String nextWeekPlan) { this.nextWeekPlan = nextWeekPlan; }
    
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}