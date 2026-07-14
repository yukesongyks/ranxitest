package com.example.demo.repository;

import com.example.demo.entity.WeeklyReport;
import com.example.demo.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<WeeklyReport, Long> {

    /**
     * 根据作者ID查询周报列表
     */
    Page<WeeklyReport> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * 根据状态查询周报列表
     */
    Page<WeeklyReport> findByStatus(ReportStatus status, Pageable pageable);

    /**
     * 根据作者ID和状态查询
     */
    Optional<WeeklyReport> findByAuthorIdAndStatus(Long authorId, ReportStatus status);

    /**
     * 查询指定周内作者是否已提交周报（防重）
     */
    @Query("SELECT COUNT(r) > 0 FROM WeeklyReport r WHERE r.authorId = :authorId " +
           "AND r.weekStartDate >= :weekStart AND r.weekStartDate < :weekEnd " +
           "AND r.status IN :statuses")
    boolean existsByAuthorIdAndWeekAndStatusIn(
            @Param("authorId") Long authorId,
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd,
            @Param("statuses") List<ReportStatus> statuses);

    /**
     * 统计指定周内已提交周报的员工数
     */
    @Query("SELECT COUNT(DISTINCT r.authorId) FROM WeeklyReport r " +
           "WHERE r.weekStartDate >= :weekStart AND r.weekStartDate < :weekEnd " +
           "AND r.status IN :statuses")
    long countSubmittedMembers(
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd,
            @Param("statuses") List<ReportStatus> statuses);

    /**
     * 统计指定周内审核通过的周报数
     */
    @Query("SELECT COUNT(r) FROM WeeklyReport r " +
           "WHERE r.weekStartDate >= :weekStart AND r.weekStartDate < :weekEnd " +
           "AND r.status = 'APPROVED'")
    long countApprovedReports(
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd);

    /**
     * 统计指定周内已提交的周报总数
     */
    @Query("SELECT COUNT(r) FROM WeeklyReport r " +
           "WHERE r.weekStartDate >= :weekStart AND r.weekStartDate < :weekEnd " +
           "AND r.status IN :statuses")
    long countSubmittedReports(
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd,
            @Param("statuses") List<ReportStatus> statuses);
}