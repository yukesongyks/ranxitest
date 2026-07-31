package com.example.myapp.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 接口调用日志实体（埋点记录）。
 * 冗余存储人员维度字段，避免统计查询时频繁 JOIN User 表。
 */
@Entity
@Table(name = "call_log",
        indexes = {
                @Index(name = "idx_call_log_api_name", columnList = "api_name"),
                @Index(name = "idx_call_log_user_id", columnList = "user_id"),
                @Index(name = "idx_call_log_call_time", columnList = "call_time"),
                @Index(name = "idx_call_log_user_type", columnList = "user_type"),
                @Index(name = "idx_call_log_department", columnList = "department")
        })
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false, length = 32)
    private String apiName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "user_type", length = 32)
    private String userType;

    @Column(name = "user_level", length = 32)
    private String userLevel;

    @Column(name = "department", length = 64)
    private String department;

    @Column(name = "call_time", nullable = false)
    private LocalDateTime callTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "result", length = 16)
    private String result;

    @Column(name = "gmt_create", nullable = false, updatable = false)
    private LocalDateTime gmtCreate;

    @PrePersist
    protected void onCreate() {
        if (this.callTime == null) {
            this.callTime = LocalDateTime.now(ZoneOffset.UTC);
        }
        this.gmtCreate = LocalDateTime.now(ZoneOffset.UTC);
    }

    public CallLog() {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public LocalDateTime getCallTime() {
        return callTime;
    }

    public void setCallTime(LocalDateTime callTime) {
        this.callTime = callTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}
