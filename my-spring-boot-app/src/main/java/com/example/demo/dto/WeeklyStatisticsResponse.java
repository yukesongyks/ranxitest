package com.example.demo.dto;

import java.math.BigDecimal;

public class WeeklyStatisticsResponse {
    private BigDecimal submitRate;
    private BigDecimal approvalRate;
    private long totalMembers;
    private long submittedMembers;
    
    public BigDecimal getSubmitRate() { return submitRate; }
    public void setSubmitRate(BigDecimal submitRate) { this.submitRate = submitRate; }
    
    public BigDecimal getApprovalRate() { return approvalRate; }
    public void setApprovalRate(BigDecimal approvalRate) { this.approvalRate = approvalRate; }
    
    public long getTotalMembers() { return totalMembers; }
    public void setTotalMembers(long totalMembers) { this.totalMembers = totalMembers; }
    
    public long getSubmittedMembers() { return submittedMembers; }
    public void setSubmittedMembers(long submittedMembers) { this.submittedMembers = submittedMembers; }
}