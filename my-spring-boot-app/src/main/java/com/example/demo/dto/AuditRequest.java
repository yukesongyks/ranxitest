package com.example.demo.dto;

public class AuditRequest {
    private String action;
    private String rejectReason;
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}