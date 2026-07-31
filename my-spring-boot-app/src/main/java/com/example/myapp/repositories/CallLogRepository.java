package com.example.myapp.repositories;

import com.example.myapp.models.CallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 接口调用日志 Repository，提供多维度统计查询。
 */
@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Long> {

    /**
     * 按维度字段聚合统计调用次数。
     *
     * @param dimensionColumn 维度列名（user_type / user_level / department / api_name）
     * @return 统计项列表，每项含 [label, count]
     */
    @Query("SELECT CASE WHEN c.userType IS NULL THEN '未知' ELSE c.userType END AS label, COUNT(c) AS cnt " +
            "FROM CallLog c GROUP BY c.userType ORDER BY cnt DESC")
    List<Object[]> countByUserType();

    @Query("SELECT CASE WHEN c.userLevel IS NULL THEN '未知' ELSE c.userLevel END AS label, COUNT(c) AS cnt " +
            "FROM CallLog c GROUP BY c.userLevel ORDER BY cnt DESC")
    List<Object[]> countByUserLevel();

    @Query("SELECT CASE WHEN c.department IS NULL THEN '未知' ELSE c.department END AS label, COUNT(c) AS cnt " +
            "FROM CallLog c GROUP BY c.department ORDER BY cnt DESC")
    List<Object[]> countByDepartment();

    @Query("SELECT c.apiName AS label, COUNT(c) AS cnt FROM CallLog c GROUP BY c.apiName ORDER BY cnt DESC")
    List<Object[]> countByApiName();

    /**
     * 折线图：按日期+维度聚合，返回时间趋势数据。
     */
    @Query("SELECT CAST(c.callTime AS date) AS day, " +
            "CASE WHEN c.userType IS NULL THEN '未知' ELSE c.userType END AS label, COUNT(c) AS cnt " +
            "FROM CallLog c WHERE c.callTime BETWEEN :start AND :end " +
            "GROUP BY day, c.userType ORDER BY day ASC")
    List<Object[]> trendByUserType(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("SELECT CAST(c.callTime AS date) AS day, " +
            "CASE WHEN c.userLevel IS NULL THEN '未知' ELSE c.userLevel END AS label, COUNT(c) AS cnt " +
            "FROM CallLog c WHERE c.callTime BETWEEN :start AND :end " +
            "GROUP BY day, c.userLevel ORDER BY day ASC")
    List<Object[]> trendByUserLevel(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT CAST(c.callTime AS date) AS day, " +
            "CASE WHEN c.department IS NULL THEN '未知' ELSE c.department END AS label, COUNT(c) AS cnt " +
            "FROM CallLog c WHERE c.callTime BETWEEN :start AND :end " +
            "GROUP BY day, c.department ORDER BY day ASC")
    List<Object[]> trendByDepartment(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    @Query("SELECT CAST(c.callTime AS date) AS day, c.apiName AS label, COUNT(c) AS cnt " +
            "FROM CallLog c WHERE c.callTime BETWEEN :start AND :end " +
            "GROUP BY day, c.apiName ORDER BY day ASC")
    List<Object[]> trendByApiName(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);
}
