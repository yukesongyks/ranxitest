package com.example.myapp.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_logs")
public class ApiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false, length = 50)
    private String apiName;

    @Column(name = "caller_name", length = 100)
    private String callerName;

    @Column(name = "user_type", length = 50)
    private String userType;

    @Column(name = "user_level", length = 50)
    private String userLevel;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "called_at", nullable = false, updatable = false)
    private LocalDateTime calledAt;

    @PrePersist
    protected void onCreate() {
        calledAt = LocalDateTime.now();
    }

    public ApiCallLog() {
    }

    public ApiCallLog(String apiName, String callerName, String userType, String userLevel, String department) {
        this.apiName = apiName;
        this.callerName = callerName;
        this.userType = userType;
        this.userLevel = userLevel;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }
}
