package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReportCreateRequest {
    
    @NotBlank(message = "本周工作内容不能为空")
    @Size(min = 10, message = "本周工作内容字数需大于10字")
    private String thisWeekWork;
    
    @NotBlank(message = "下周计划不能为空")
    @Size(min = 10, message = "下周计划字数需大于10字")
    private String nextWeekPlan;
    
    public String getThisWeekWork() { return thisWeekWork; }
    public void setThisWeekWork(String thisWeekWork) { this.thisWeekWork = thisWeekWork; }
    
    public String getNextWeekPlan() { return nextWeekPlan; }
    public void setNextWeekPlan(String nextWeekPlan) { this.nextWeekPlan = nextWeekPlan; }
}