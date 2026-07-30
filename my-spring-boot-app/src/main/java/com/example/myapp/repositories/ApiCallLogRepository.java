package com.example.myapp.repositories;

import com.example.myapp.models.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    long countByApiName(String apiName);

    @Query("SELECT a.userType AS value, COUNT(a) AS cnt FROM ApiCallLog a WHERE a.userType IS NOT NULL GROUP BY a.userType")
    List<Object[]> countByUserType();

    @Query("SELECT a.userLevel AS value, COUNT(a) AS cnt FROM ApiCallLog a WHERE a.userLevel IS NOT NULL GROUP BY a.userLevel")
    List<Object[]> countByUserLevel();

    @Query("SELECT a.department AS value, COUNT(a) AS cnt FROM ApiCallLog a WHERE a.department IS NOT NULL GROUP BY a.department")
    List<Object[]> countByDepartment();

    @Query("SELECT FUNCTION('DATE', a.calledAt) AS d, COUNT(a) AS cnt FROM ApiCallLog a GROUP BY FUNCTION('DATE', a.calledAt) ORDER BY d")
    List<Object[]> countByDay();
}
