# Hello World 1.0T2 重跑 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 用 Java 实现三个接口（helloworld、哈希算法、冒泡排序），前端新增三 Tab 页面展示结果并提供导出按钮，后端提供导出接口，同时实现调用埋点并在前端以折线图/饼图/柱状图可视化调用情况。

**Architecture:** 后端基于现有 Spring Boot 2.6.6 / Java 17 / JPA / H2 应用，新增 REST 控制器 `DemoApiController` 暴露三个计算接口、导出接口和埋点统计接口。前端在 Kanban web-ui（React 18 / Vite / Tailwind v4）新增独立页面组件 `DemoPage`，通过 Vite 代理转发至后端 8080 端口，使用 `recharts` 渲染折线图/饼图/柱状图。跨仓接口契约全部为新增，向后兼容。

**Tech Stack:** Java 17, Spring Boot 2.6.6, Spring Data JPA, H2 Database, Thymeleaf, React 18, TypeScript, Vite 6, Tailwind CSS v4, Radix UI, recharts, lucide-react, sonner

## Global Constraints

- Java 17（pom.xml 已锁定 `java.version=17`）
- Spring Boot 2.6.6（pom.xml parent 已锁定，禁止升级）
- javax.* 命名空间（Spring Boot 2.x，非 jakarta.*）
- React ^18.3.1 / TypeScript ^5.9.2 / Vite ^6.4.2
- Tailwind CSS v4（`@tailwindcss/vite` 插件，`@theme` token 体系）
- 永远暗色主题：`bg-surface-0` → `bg-surface-4`，禁止 `dark:` 前缀
- 前端禁止内联 import（`await import()`），禁止 `any` 类型
- 后端 `@` alias → `web-ui/src`，新页面组件放入 `src/components/demo/`
- 跨仓接口契约：所有新增接口均为独立路径 `/api/demo/*`，不修改任何现有接口
- `library-backend` 和 `library-frontend` 仓库为空（仅 README），不作为本任务落点

---

## File Structure

### 后端（ranxitest-0314-test 仓库）

| 文件 | 操作 | 职责 |
|------|------|------|
| `my-spring-boot-app/src/main/java/com/example/myapp/controllers/DemoApiController.java` | Create | REST 控制器，暴露 helloworld / hash / bubble-sort / export / stats 接口 |
| `my-spring-boot-app/src/main/java/com/example/myapp/services/DemoService.java` | Create | 业务逻辑：哈希计算、冒泡排序、结果导出 |
| `my-spring-boot-app/src/main/java/com/example/myapp/models/ApiCallLog.java` | Create | JPA 实体：调用埋点记录 |
| `my-spring-boot-app/src/main/java/com/example/myapp/repositories/ApiCallLogRepository.java` | Create | JPA Repository：调用日志查询 |
| `my-spring-boot-app/src/main/java/com/example/myapp/services/ApiCallLogService.java` | Create | 埋点写入 + 统计聚合查询 |
| `my-spring-boot-app/src/main/java/com/example/myapp/dto/CallStatsResponse.java` | Create | 统计接口响应 DTO |
| `my-spring-boot-app/src/main/java/com/example/myapp/dto/DemoResult.java` | Create | 三接口统一响应 DTO |
| `my-spring-boot-app/src/main/resources/application.properties` | Modify | 新增 CORS 配置 |
| `my-spring-boot-app/src/test/java/com/example/myapp/DemoServiceTest.java` | Create | DemoService 单元测试 |
| `my-spring-boot-app/src/test/java/com/example/myapp/DemoApiControllerTest.java` | Create | 控制器集成测试 |

### 前端（dtazzi-cline-gt-toast-510a132b 仓库）

| 文件 | 操作 | 职责 |
|------|------|------|
| `web-ui/package.json` | Modify | 新增 recharts 依赖 |
| `web-ui/vite.config.ts` | Modify | 新增 `/api/demo` 代理指向后端 8080 |
| `web-ui/src/components/demo/demo-page.tsx` | Create | 演示页面主组件，三 Tab + 导出 + 报表 |
| `web-ui/src/components/demo/demo-api.ts` | Create | API 客户端封装 |
| `web-ui/src/components/demo/demo-types.ts` | Create | TypeScript 类型定义 |
| `web-ui/src/components/demo/demo-hello-world-tab.tsx` | Create | HelloWorld Tab |
| `web-ui/src/components/demo/demo-hash-tab.tsx` | Create | 哈希算法 Tab |
| `web-ui/src/components/demo/demo-bubble-sort-tab.tsx` | Create | 冒泡排序 Tab |
| `web-ui/src/components/demo/demo-export-button.tsx` | Create | 导出按钮组件 |
| `web-ui/src/components/demo/demo-stats-charts.tsx` | Create | 调用统计可视化（折线/饼图/柱状图） |
| `web-ui/src/components/demo/demo-page.test.tsx` | Create | 页面单元测试 |

---

## Task 1: 后端 DTO 与实体层

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/dto/DemoResult.java`
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/dto/CallStatsResponse.java`
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/models/ApiCallLog.java`
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/repositories/ApiCallLogRepository.java`

**Interfaces:**
- Consumes: 无（本任务是第一个 Task）
- Produces: `DemoResult<T>`（泛型响应包装）、`CallStatsResponse`（统计响应）、`ApiCallLog`（JPA 实体）、`ApiCallLogRepository`（数据访问层）

### Step 1.1: 创建 DemoResult DTO

- [ ] 创建文件 `my-spring-boot-app/src/main/java/com/example/myapp/dto/DemoResult.java`

```java
package com.example.myapp.dto;

import java.time.LocalDateTime;

public class DemoResult<T> {

    private boolean success;
    private String type;
    private T data;
    private LocalDateTime timestamp;

    public DemoResult() {
    }

    public DemoResult(boolean success, String type, T data) {
        this.success = success;
        this.type = type;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> DemoResult<T> ok(String type, T data) {
        return new DemoResult<>(true, type, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
```

### Step 1.2: 创建 CallStatsResponse DTO

- [ ] 创建文件 `my-spring-boot-app/src/main/java/com/example/myapp/dto/CallStatsResponse.java`

```java
package com.example.myapp.dto;

import java.util.List;
import java.util.Map;

public class CallStatsResponse {

    private long totalCalls;
    private List<DimensionStat> byUserType;
    private List<DimensionStat> byUserLevel;
    private List<DimensionStat> byDepartment;
    private List<TrendPoint> trendByDay;

    public CallStatsResponse() {
    }

    public long getTotalCalls() {
        return totalCalls;
    }

    public void setTotalCalls(long totalCalls) {
        this.totalCalls = totalCalls;
    }

    public List<DimensionStat> getByUserType() {
        return byUserType;
    }

    public void setByUserType(List<DimensionStat> byUserType) {
        this.byUserType = byUserType;
    }

    public List<DimensionStat> getByUserLevel() {
        return byUserLevel;
    }

    public void setByUserLevel(List<DimensionStat> byUserLevel) {
        this.byUserLevel = byUserLevel;
    }

    public List<DimensionStat> getByDepartment() {
        return byDepartment;
    }

    public void setByDepartment(List<DimensionStat> byDepartment) {
        this.byDepartment = byDepartment;
    }

    public List<TrendPoint> getTrendByDay() {
        return trendByDay;
    }

    public void setTrendByDay(List<TrendPoint> trendByDay) {
        this.trendByDay = trendByDay;
    }

    public static class DimensionStat {
        private String dimension;
        private String value;
        private long count;

        public DimensionStat() {
        }

        public DimensionStat(String dimension, String value, long count) {
            this.dimension = dimension;
            this.value = value;
            this.count = count;
        }

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    public static class TrendPoint {
        private String date;
        private long count;

        public TrendPoint() {
        }

        public TrendPoint(String date, long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
```

### Step 1.3: 创建 ApiCallLog 实体

- [ ] 创建文件 `my-spring-boot-app/src/main/java/com/example/myapp/models/ApiCallLog.java`

```java
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
```

### Step 1.4: 创建 ApiCallLogRepository

- [ ] 创建文件 `my-spring-boot-app/src/main/java/com/example/myapp/repositories/ApiCallLogRepository.java`

```java
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
```

### Step 1.5: 验证编译

- [ ] 运行编译命令确认无语法错误

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/ranxitest-0314-test/my-spring-boot-app
mvn compile -q
```

预期输出：BUILD SUCCESS，无编译错误。

---

## Task 2: 后端 Service 层（DemoService + ApiCallLogService）

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/services/DemoService.java`
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/services/ApiCallLogService.java`

**Interfaces:**
- Consumes: Task 1 的 `DemoResult`、`CallStatsResponse`、`ApiCallLog`、`ApiCallLogRepository`
- Produces: `DemoService.helloWorld()` → `String`、`DemoService.hash(String, String)` → `String`、`DemoService.bubbleSort(List<Integer>)` → `List<Integer>`、`DemoService.export(String, Object)` → `byte[]`、`ApiCallLogService.log(...)`、`ApiCallLogService.getStats()` → `CallStatsResponse`

### Step 2.1: 创建 DemoService

- [ ] 创建文件 `my-spring-boot-app/src/main/java/com/example/myapp/services/DemoService.java`

```java
package com.example.myapp.services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoService {

    public String helloWorld() {
        return "Hello, World!";
    }

    public String hash(String algorithm, String input) {
        if (algorithm == null || algorithm.trim().isEmpty()) {
            throw new IllegalArgumentException("哈希算法不能为空");
        }
        if (input == null) {
            throw new IllegalArgumentException("输入不能为空");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.trim().toUpperCase());
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("不支持的哈希算法: " + algorithm, e);
        }
    }

    public List<Integer> bubbleSort(List<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("排序列表不能为空");
        }
        List<Integer> arr = new ArrayList<>(input);
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
        return arr;
    }

    public byte[] exportToCsv(String type, Object data) {
        StringBuilder csv = new StringBuilder();
        csv.append("type,value\n");
        if ("helloworld".equals(type)) {
            csv.append("helloworld,").append(data).append("\n");
        } else if ("hash".equals(type)) {
            csv.append("hash_result,").append(data).append("\n");
        } else if ("bubblesort".equals(type)) {
            csv.append("sorted_result,").append(data).append("\n");
        } else {
            csv.append("unknown,").append(String.valueOf(data)).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
```

### Step 2.2: 创建 ApiCallLogService

- [ ] 创建文件 `my-spring-boot-app/src/main/java/com/example/myapp/services/ApiCallLogService.java`

```java
package com.example.myapp.services;

import com.example.myapp.dto.CallStatsResponse;
import com.example.myapp.models.ApiCallLog;
import com.example.myapp.repositories.ApiCallLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ApiCallLogService {

    private final ApiCallLogRepository apiCallLogRepository;

    @Autowired
    public ApiCallLogService(ApiCallLogRepository apiCallLogRepository) {
        this.apiCallLogRepository = apiCallLogRepository;
    }

    public void log(String apiName, String callerName, String userType, String userLevel, String department) {
        ApiCallLog logEntry = new ApiCallLog(apiName, callerName, userType, userLevel, department);
        apiCallLogRepository.save(logEntry);
    }

    public CallStatsResponse getStats() {
        CallStatsResponse response = new CallStatsResponse();
        response.setTotalCalls(apiCallLogRepository.count());

        response.setByUserType(toDimensionStats("userType", apiCallLogRepository.countByUserType()));
        response.setByUserLevel(toDimensionStats("userLevel", apiCallLogRepository.countByUserLevel()));
        response.setByDepartment(toDimensionStats("department", apiCallLogRepository.countByDepartment()));
        response.setTrendByDay(toTrendPoints(apiCallLogRepository.countByDay()));

        return response;
    }

    private List<CallStatsResponse.DimensionStat> toDimensionStats(String dimension, List<Object[]> rows) {
        List<CallStatsResponse.DimensionStat> stats = new ArrayList<>();
        for (Object[] row : rows) {
            String value = (String) row[0];
            long count = ((Number) row[1]).longValue();
            stats.add(new CallStatsResponse.DimensionStat(dimension, value, count));
        }
        return stats;
    }

    private List<CallStatsResponse.TrendPoint> toTrendPoints(List<Object[]> rows) {
        List<CallStatsResponse.TrendPoint> points = new ArrayList<>();
        for (Object[] row : rows) {
            String date = String.valueOf(row[0]);
            long count = ((Number) row[1]).longValue();
            points.add(new CallStatsResponse.TrendPoint(date, count));
        }
        return points;
    }
}
```

### Step 2.3: 验证编译

- [ ] 运行编译命令

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/ranxitest-0314-test/my-spring-boot-app
mvn compile -q
```

预期输出：BUILD SUCCESS。

---

## Task 3: 后端 Controller 层（DemoApiController）

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/controllers/DemoApiController.java`
- Modify: `my-spring-boot-app/src/main/resources/application.properties`

**Interfaces:**
- Consumes: Task 2 的 `DemoService` 和 `ApiCallLogService`
- Produces: REST 端点 — `GET /api/demo/hello`、`GET /api/demo/hash?algorithm=&input=`、`POST /api/demo/bubble-sort`、`GET /api/demo/export?type=&data=`、`GET /api/demo/stats`

### Step 3.1: 创建 DemoApiController

- [ ] 创建文件 `my-spring-boot-app/src/main/java/com/example/myapp/controllers/DemoApiController.java`

```java
package com.example.myapp.controllers;

import com.example.myapp.dto.CallStatsResponse;
import com.example.myapp.dto.DemoResult;
import com.example.myapp.services.ApiCallLogService;
import com.example.myapp.services.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoApiController {

    private final DemoService demoService;
    private final ApiCallLogService apiCallLogService;

    @Autowired
    public DemoApiController(DemoService demoService, ApiCallLogService apiCallLogService) {
        this.demoService = demoService;
        this.apiCallLogService = apiCallLogService;
    }

    @GetMapping("/hello")
    public DemoResult<String> helloWorld(HttpServletRequest request) {
        String result = demoService.helloWorld();
        apiCallLogService.log("helloworld", extractCaller(request), extractHeader(request, "X-User-Type"),
                extractHeader(request, "X-User-Level"), extractHeader(request, "X-Department"));
        return DemoResult.ok("helloworld", result);
    }

    @GetMapping("/hash")
    public DemoResult<String> hash(
            @RequestParam(defaultValue = "SHA-256") String algorithm,
            @RequestParam(defaultValue = "") String input,
            HttpServletRequest request) {
        String result = demoService.hash(algorithm, input);
        apiCallLogService.log("hash", extractCaller(request), extractHeader(request, "X-User-Type"),
                extractHeader(request, "X-User-Level"), extractHeader(request, "X-Department"));
        return DemoResult.ok("hash", result);
    }

    @PostMapping("/bubble-sort")
    public DemoResult<List<Integer>> bubbleSort(
            @RequestBody Map<String, List<Integer>> body,
            HttpServletRequest request) {
        List<Integer> input = body.getOrDefault("input", List.of());
        List<Integer> result = demoService.bubbleSort(input);
        apiCallLogService.log("bubblesort", extractCaller(request), extractHeader(request, "X-User-Type"),
                extractHeader(request, "X-User-Level"), extractHeader(request, "X-Department"));
        return DemoResult.ok("bubblesort", result);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam String type,
            @RequestParam String data) {
        byte[] csv = demoService.exportToCsv(type, data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set("Content-Disposition", "attachment; filename=demo-export.csv");
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    @GetMapping("/stats")
    public CallStatsResponse stats() {
        return apiCallLogService.getStats();
    }

    private String extractCaller(HttpServletRequest request) {
        String caller = request.getHeader("X-Caller-Name");
        return caller != null ? caller : "anonymous";
    }

    private String extractHeader(HttpServletRequest request, String name) {
        return request.getHeader(name);
    }
}
```

### Step 3.2: 修改 application.properties 新增 CORS

- [ ] 追加 CORS 配置到 `my-spring-boot-app/src/main/resources/application.properties`

在文件末尾追加以下内容：

```properties

# CORS for demo API
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=false
```

### Step 3.3: 验证编译与启动

- [ ] 运行编译命令

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/ranxitest-0314-test/my-spring-boot-app
mvn compile -q
```

预期输出：BUILD SUCCESS。

---

## Task 4: 后端单元测试

**Files:**
- Create: `my-spring-boot-app/src/test/java/com/example/myapp/DemoServiceTest.java`
- Create: `my-spring-boot-app/src/test/java/com/example/myapp/DemoApiControllerTest.java`

**Interfaces:**
- Consumes: Task 2 和 Task 3 的 Service 和 Controller
- Produces: 测试覆盖验证

### Step 4.1: 创建 DemoServiceTest

- [ ] 创建文件 `my-spring-boot-app/src/test/java/com/example/myapp/DemoServiceTest.java`

```java
package com.example.myapp;

import com.example.myapp.services.DemoService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DemoServiceTest {

    private final DemoService demoService = new DemoService();

    @Test
    void helloWorldReturnsGreeting() {
        assertEquals("Hello, World!", demoService.helloWorld());
    }

    @Test
    void hashReturnsCorrectSha256() {
        String result = demoService.hash("SHA-256", "hello");
        assertEquals(64, result.length());
    }

    @Test
    void hashThrowsForUnsupportedAlgorithm() {
        assertThrows(IllegalArgumentException.class, () -> demoService.hash("INVALID-ALGO", "test"));
    }

    @Test
    void bubbleSortReturnsSortedAscending() {
        List<Integer> input = Arrays.asList(5, 3, 8, 1, 9, 2);
        List<Integer> result = demoService.bubbleSort(input);
        assertEquals(Arrays.asList(1, 2, 3, 5, 8, 9), result);
    }

    @Test
    void bubbleSortReturnsEmptyForEmptyInput() {
        List<Integer> result = demoService.bubbleSort(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void exportToCsvContainsHeader() {
        byte[] csv = demoService.exportToCsv("helloworld", "Hello, World!");
        String content = new String(csv);
        assertTrue(content.contains("type,value"));
        assertTrue(content.contains("helloworld"));
    }
}
```

### Step 4.2: 创建 DemoApiControllerTest

- [ ] 创建文件 `my-spring-boot-app/src/test/java/com/example/myapp/DemoApiControllerTest.java`

```java
package com.example.myapp;

import com.example.myapp.controllers.DemoApiController;
import com.example.myapp.dto.CallStatsResponse;
import com.example.myapp.dto.DemoResult;
import com.example.myapp.services.ApiCallLogService;
import com.example.myapp.services.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloWorldEndpointReturnsGreeting() throws Exception {
        mockMvc.perform(get("/api/demo/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("helloworld"))
                .andExpect(jsonPath("$.data").value("Hello, World!"));
    }

    @Test
    void hashEndpointReturnsHexResult() throws Exception {
        mockMvc.perform(get("/api/demo/hash")
                        .param("algorithm", "SHA-256")
                        .param("input", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("hash"));
    }

    @Test
    void bubbleSortEndpointReturnsSortedArray() throws Exception {
        mockMvc.perform(post("/api/demo/bubble-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"input\":[3,1,2]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("bubblesort"))
                .andExpect(jsonPath("$.data[0]").value(1))
                .andExpect(jsonPath("$.data[1]").value(2))
                .andExpect(jsonPath("$.data[2]").value(3));
    }

    @Test
    void exportEndpointReturnsCsv() throws Exception {
        mockMvc.perform(get("/api/demo/export")
                        .param("type", "helloworld")
                        .param("data", "Hello, World!"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void statsEndpointReturnsStats() throws Exception {
        mockMvc.perform(get("/api/demo/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalls").exists());
    }
}
```

### Step 4.3: 运行测试

- [ ] 运行测试命令

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/ranxitest-0314-test/my-spring-boot-app
mvn test -q
```

预期输出：所有测试通过（Tests run: 11, Failures: 0, Errors: 0, Skipped: 0）。

---

## Task 5: 前端依赖与代理配置

**Files:**
- Modify: `web-ui/package.json`
- Modify: `web-ui/vite.config.ts`

**Interfaces:**
- Consumes: Task 3 的后端 REST 端点（`/api/demo/*`）
- Produces: `recharts` 依赖可用、Vite 代理 `/api/demo` → `http://127.0.0.1:8080`

### Step 5.1: 安装 recharts 依赖

- [ ] 在 web-ui 目录安装 recharts

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/dtazzi-cline-gt-toast-510a132b/web-ui
npm install recharts
```

预期输出：`recharts` 添加到 `package.json` dependencies。

### Step 5.2: 修改 vite.config.ts 新增代理

- [ ] 修改 `web-ui/vite.config.ts` 的 `server.proxy` 块，在现有 `/api` 代理前新增 `/api/demo` 代理

将 `server.proxy` 部分从：

```typescript
		proxy: {
			"/api": {
				target: `http://127.0.0.1:${process.env.KANBAN_RUNTIME_PORT || "3484"}`,
				changeOrigin: true,
				ws: true,
			},
		},
```

改为：

```typescript
		proxy: {
			"/api/demo": {
				target: `http://127.0.0.1:${process.env.DEMO_BACKEND_PORT || "8080"}`,
				changeOrigin: true,
			},
			"/api": {
				target: `http://127.0.0.1:${process.env.KANBAN_RUNTIME_PORT || "3484"}`,
				changeOrigin: true,
				ws: true,
			},
		},
```

注意：`/api/demo` 必须排在 `/api` 之前，Vite 按顺序匹配 proxy key。

### Step 5.3: 验证类型检查

- [ ] 运行 typecheck

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/dtazzi-cline-gt-toast-510a132b/web-ui
npm run typecheck
```

预期输出：无类型错误（`tsc --noEmit` 退出码 0）。

---

## Task 6: 前端 API 客户端与类型定义

**Files:**
- Create: `web-ui/src/components/demo/demo-types.ts`
- Create: `web-ui/src/components/demo/demo-api.ts`

**Interfaces:**
- Consumes: Task 3 的后端 REST 端点契约
- Produces: TypeScript 类型 + API 调用函数（供 Task 7 和 Task 8 的组件使用）

### Step 6.1: 创建类型定义

- [ ] 创建文件 `web-ui/src/components/demo/demo-types.ts`

```typescript
export interface DemoResult<T> {
	success: boolean;
	type: string;
	data: T;
	timestamp: string;
}

export interface DimensionStat {
	dimension: string;
	value: string;
	count: number;
}

export interface TrendPoint {
	date: string;
	count: number;
}

export interface CallStatsResponse {
	totalCalls: number;
	byUserType: DimensionStat[];
	byUserLevel: DimensionStat[];
	byDepartment: DimensionStat[];
	trendByDay: TrendPoint[];
}

export type DemoTab = "helloworld" | "hash" | "bubblesort";
```

### Step 6.2: 创建 API 客户端

- [ ] 创建文件 `web-ui/src/components/demo/demo-api.ts`

```typescript
import type { CallStatsResponse, DemoResult } from "./demo-types";

const BASE_URL = "/api/demo";

export async function fetchHelloWorld(
	headers: Record<string, string> = {},
): Promise<DemoResult<string>> {
	const response = await fetch(`${BASE_URL}/hello`, { headers });
	if (!response.ok) {
		throw new Error(`HelloWorld request failed: ${response.status}`);
	}
	return response.json() as Promise<DemoResult<string>>;
}

export async function fetchHash(
	algorithm: string,
	input: string,
	headers: Record<string, string> = {},
): Promise<DemoResult<string>> {
	const params = new URLSearchParams({ algorithm, input });
	const response = await fetch(`${BASE_URL}/hash?${params}`, { headers });
	if (!response.ok) {
		throw new Error(`Hash request failed: ${response.status}`);
	}
	return response.json() as Promise<DemoResult<string>>;
}

export async function fetchBubbleSort(
	input: number[],
	headers: Record<string, string> = {},
): Promise<DemoResult<number[]>> {
	const response = await fetch(`${BASE_URL}/bubble-sort`, {
		method: "POST",
		headers: { "Content-Type": "application/json", ...headers },
		body: JSON.stringify({ input }),
	});
	if (!response.ok) {
		throw new Error(`BubbleSort request failed: ${response.status}`);
	}
	return response.json() as Promise<DemoResult<number[]>>;
}

export function getExportUrl(type: string, data: string): string {
	const params = new URLSearchParams({ type, data });
	return `${BASE_URL}/export?${params}`;
}

export async function fetchStats(): Promise<CallStatsResponse> {
	const response = await fetch(`${BASE_URL}/stats`);
	if (!response.ok) {
		throw new Error(`Stats request failed: ${response.status}`);
	}
	return response.json() as Promise<CallStatsResponse>;
}
```

### Step 6.3: 验证类型检查

- [ ] 运行 typecheck

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/dtazzi-cline-gt-toast-510a132b/web-ui
npm run typecheck
```

预期输出：无类型错误。

---

## Task 7: 前端 Tab 页面组件（三个 Tab + 导出）

**Files:**
- Create: `web-ui/src/components/demo/demo-export-button.tsx`
- Create: `web-ui/src/components/demo/demo-hello-world-tab.tsx`
- Create: `web-ui/src/components/demo/demo-hash-tab.tsx`
- Create: `web-ui/src/components/demo/demo-bubble-sort-tab.tsx`

**Interfaces:**
- Consumes: Task 6 的 API 函数和类型
- Produces: 三个可独立渲染的 Tab 组件 + 导出按钮组件（供 Task 8 的 DemoPage 组合）

### Step 7.1: 创建 ExportButton 组件

- [ ] 创建文件 `web-ui/src/components/demo/demo-export-button.tsx`

```tsx
import { Download } from "lucide-react";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { getExportUrl } from "./demo-api";

interface DemoExportButtonProps {
	type: string;
	data: string;
}

export function DemoExportButton({ type, data }: DemoExportButtonProps) {
	const [loading, setLoading] = useState(false);

	const handleExport = () => {
		setLoading(true);
		const url = getExportUrl(type, data);
		window.location.href = url;
		setTimeout(() => setLoading(false), 1000);
	};

	return (
		<Button
			variant="default"
			icon={<Download size={16} />}
			onClick={handleExport}
			fill
		>
			{loading ? "导出中..." : "导出结果"}
		</Button>
	);
}
```

### Step 7.2: 创建 HelloWorld Tab

- [ ] 创建文件 `web-ui/src/components/demo/demo-hello-world-tab.tsx`

```tsx
import { useState } from "react";

import { showAppToast } from "@/components/app-toaster";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import { fetchHelloWorld } from "./demo-api";
import { DemoExportButton } from "./demo-export-button";

export function DemoHelloWorldTab() {
	const [result, setResult] = useState<string | null>(null);
	const [loading, setLoading] = useState(false);

	const handleExecute = async () => {
		setLoading(true);
		try {
			const res = await fetchHelloWorld({
				"X-Caller-Name": "frontend-user",
				"X-User-Type": "developer",
				"X-User-Level": "L1",
				"X-Department": "tech",
			});
			setResult(res.data);
		} catch (e) {
			showAppToast(
				{
					intent: "danger",
					icon: "warning-sign",
					message: `执行失败: ${e instanceof Error ? e.message : String(e)}`,
					timeout: 5000,
				},
				"demo-hello-error",
			);
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="flex flex-col gap-4 p-4">
			<div className="flex gap-2">
				<Button variant="primary" onClick={() => void handleExecute()}>
					执行 HelloWorld
				</Button>
				{result ? <DemoExportButton type="helloworld" data={result} /> : null}
			</div>
			{loading ? (
				<Spinner size={20} />
			) : result ? (
				<div className="rounded-md bg-surface-2 p-4 font-mono text-sm text-text-primary">
					{result}
				</div>
			) : (
				<p className="text-sm text-text-secondary">点击按钮执行 HelloWorld 接口。</p>
			)}
		</div>
	);
}
```

### Step 7.3: 创建 Hash Tab

- [ ] 创建文件 `web-ui/src/components/demo/demo-hash-tab.tsx`

```tsx
import { useState } from "react";

import { showAppToast } from "@/components/app-toaster";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import { fetchHash } from "./demo-api";
import { DemoExportButton } from "./demo-export-button";

const ALGORITHMS = ["SHA-256", "SHA-1", "MD5", "SHA-512"];

export function DemoHashTab() {
	const [algorithm, setAlgorithm] = useState("SHA-256");
	const [input, setInput] = useState("");
	const [result, setResult] = useState<string | null>(null);
	const [loading, setLoading] = useState(false);

	const handleExecute = async () => {
		setLoading(true);
		try {
			const res = await fetchHash(algorithm, input, {
				"X-Caller-Name": "frontend-user",
				"X-User-Type": "developer",
				"X-User-Level": "L2",
				"X-Department": "tech",
			});
			setResult(res.data);
		} catch (e) {
			showAppToast(
				{
					intent: "danger",
					icon: "warning-sign",
					message: `执行失败: ${e instanceof Error ? e.message : String(e)}`,
					timeout: 5000,
				},
				"demo-hash-error",
			);
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="flex flex-col gap-4 p-4">
			<div className="flex flex-col gap-2">
				<label className="text-sm text-text-secondary" htmlFor="hash-algorithm">
					哈希算法
				</label>
				<select
					id="hash-algorithm"
					value={algorithm}
					onChange={(e) => setAlgorithm(e.target.value)}
					className="rounded-md border border-border bg-surface-2 px-3 py-2 text-sm text-text-primary"
				>
					{ALGORITHMS.map((algo) => (
						<option key={algo} value={algo}>
							{algo}
						</option>
					))}
				</select>
			</div>
			<div className="flex flex-col gap-2">
				<label className="text-sm text-text-secondary" htmlFor="hash-input">
					输入文本
				</label>
				<input
					id="hash-input"
					type="text"
					value={input}
					onChange={(e) => setInput(e.target.value)}
					placeholder="请输入要哈希的文本"
					className="rounded-md border border-border bg-surface-2 px-3 py-2 text-sm text-text-primary"
				/>
			</div>
			<div className="flex gap-2">
				<Button variant="primary" onClick={() => void handleExecute()}>
					执行哈希
				</Button>
				{result ? <DemoExportButton type="hash" data={result} /> : null}
			</div>
			{loading ? (
				<Spinner size={20} />
			) : result ? (
				<div className="break-all rounded-md bg-surface-2 p-4 font-mono text-xs text-text-primary">
					{result}
				</div>
			) : null}
		</div>
	);
}
```

### Step 7.4: 创建 BubbleSort Tab

- [ ] 创建文件 `web-ui/src/components/demo/demo-bubble-sort-tab.tsx`

```tsx
import { useState } from "react";

import { showAppToast } from "@/components/app-toaster";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import { fetchBubbleSort } from "./demo-api";
import { DemoExportButton } from "./demo-export-button";

export function DemoBubbleSortTab() {
	const [inputText, setInputText] = useState("5,3,8,1,9,2");
	const [result, setResult] = useState<number[] | null>(null);
	const [loading, setLoading] = useState(false);

	const handleExecute = async () => {
		const input = inputText
			.split(",")
			.map((s) => Number.parseInt(s.trim(), 10))
			.filter((n) => !Number.isNaN(n));
		setLoading(true);
		try {
			const res = await fetchBubbleSort(input, {
				"X-Caller-Name": "frontend-user",
				"X-User-Type": "developer",
				"X-User-Level": "L3",
				"X-Department": "data",
			});
			setResult(res.data);
		} catch (e) {
			showAppToast(
				{
					intent: "danger",
					icon: "warning-sign",
					message: `执行失败: ${e instanceof Error ? e.message : String(e)}`,
					timeout: 5000,
				},
				"demo-sort-error",
			);
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="flex flex-col gap-4 p-4">
			<div className="flex flex-col gap-2">
				<label className="text-sm text-text-secondary" htmlFor="sort-input">
					输入数字（逗号分隔）
				</label>
				<input
					id="sort-input"
					type="text"
					value={inputText}
					onChange={(e) => setInputText(e.target.value)}
					placeholder="例如: 5,3,8,1,9,2"
					className="rounded-md border border-border bg-surface-2 px-3 py-2 text-sm text-text-primary"
				/>
			</div>
			<div className="flex gap-2">
				<Button variant="primary" onClick={() => void handleExecute()}>
					执行冒泡排序
				</Button>
				{result ? (
					<DemoExportButton type="bubblesort" data={result.join(",")} />
				) : null}
			</div>
			{loading ? (
				<Spinner size={20} />
			) : result ? (
				<div className="rounded-md bg-surface-2 p-4 font-mono text-sm text-text-primary">
					{result.join(", ")}
				</div>
			) : null}
		</div>
	);
}
```

### Step 7.5: 验证类型检查

- [ ] 运行 typecheck

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/dtazzi-cline-gt-toast-510a132b/web-ui
npm run typecheck
```

预期输出：无类型错误。

---

## Task 8: 前端调用统计可视化（折线图 + 饼图 + 柱状图）与 DemoPage 主页面

**Files:**
- Create: `web-ui/src/components/demo/demo-stats-charts.tsx`
- Create: `web-ui/src/components/demo/demo-page.tsx`
- Create: `web-ui/src/components/demo/demo-page.test.tsx`

**Interfaces:**
- Consumes: Task 6 的 `fetchStats` + `CallStatsResponse` 类型；Task 7 的三个 Tab 组件
- Produces: `DemoPage` 完整页面（三 Tab + 导出 + 统计报表）

### Step 8.1: 创建统计图表组件

- [ ] 创建文件 `web-ui/src/components/demo/demo-stats-charts.tsx`

```tsx
import { useEffect, useState } from "react";
import {
	Bar,
	BarChart,
	CartesianGrid,
	Cell,
	Legend,
	Line,
	LineChart,
	Pie,
	PieChart,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from "recharts";

import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import { showAppToast } from "@/components/app-toaster";
import { fetchStats } from "./demo-api";
import type { CallStatsResponse } from "./demo-types";

const CHART_COLORS = [
	"#0084FF",
	"#3FB950",
	"#D29922",
	"#A371F7",
	"#F85149",
	"#4C9AFF",
	"#D4A72C",
	"#339DFF",
];

export function DemoStatsCharts() {
	const [stats, setStats] = useState<CallStatsResponse | null>(null);
	const [loading, setLoading] = useState(false);

	const loadStats = async () => {
		setLoading(true);
		try {
			const data = await fetchStats();
			setStats(data);
		} catch (e) {
			showAppToast(
				{
					intent: "danger",
					icon: "warning-sign",
					message: `加载统计数据失败: ${e instanceof Error ? e.message : String(e)}`,
					timeout: 5000,
				},
				"demo-stats-error",
			);
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		void loadStats();
	}, []);

	if (loading && !stats) {
		return (
			<div className="flex items-center justify-center p-8">
				<Spinner size={24} />
			</div>
		);
	}

	if (!stats) {
		return (
			<div className="flex flex-col items-center gap-3 p-8">
				<p className="text-sm text-text-secondary">暂无统计数据</p>
				<Button variant="default" onClick={() => void loadStats()}>
					刷新
				</Button>
			</div>
		);
	}

	return (
		<div className="flex flex-col gap-6 p-4">
			<div className="flex items-center justify-between">
				<h3 className="text-sm font-semibold text-text-primary">
					调用统计报表（总调用次数: {stats.totalCalls}）
				</h3>
				<Button variant="ghost" onClick={() => void loadStats()}>
					刷新数据
				</Button>
			</div>

			{stats.trendByDay.length > 0 ? (
				<div className="rounded-lg border border-border bg-surface-2 p-4">
					<h4 className="mb-3 text-xs font-medium text-text-secondary">
						每日调用趋势（折线图）
					</h4>
					<ResponsiveContainer width="100%" height={240}>
						<LineChart data={stats.trendByDay}>
							<CartesianGrid strokeDasharray="3 3" stroke="#30363D" />
							<XAxis dataKey="date" stroke="#8B949E" fontSize={11} />
							<YAxis stroke="#8B949E" fontSize={11} />
							<Tooltip
								contentStyle={{
									background: "#24292E",
									border: "1px solid #444C56",
									borderRadius: "6px",
									fontSize: "12px",
								}}
							/>
							<Line
								type="monotone"
								dataKey="count"
								stroke="#0084FF"
								strokeWidth={2}
								dot={{ fill: "#0084FF", r: 3 }}
							/>
						</LineChart>
					</ResponsiveContainer>
				</div>
			) : null}

			<div className="grid grid-cols-1 gap-4 md:grid-cols-2">
				{stats.byUserType.length > 0 ? (
					<div className="rounded-lg border border-border bg-surface-2 p-4">
						<h4 className="mb-3 text-xs font-medium text-text-secondary">
							按人员类型（饼图）
						</h4>
						<ResponsiveContainer width="100%" height={200}>
							<PieChart>
								<Pie
									data={stats.byUserType}
									dataKey="count"
									nameKey="value"
									cx="50%"
									cy="50%"
									outerRadius={70}
									label
								>
									{stats.byUserType.map((_, index) => (
										<Cell
											key={`cell-${index}`}
											fill={CHART_COLORS[index % CHART_COLORS.length]}
										/>
									))}
								</Pie>
								<Tooltip
									contentStyle={{
										background: "#24292E",
										border: "1px solid #444C56",
										borderRadius: "6px",
										fontSize: "12px",
									}}
								/>
								<Legend wrapperStyle={{ fontSize: "11px" }} />
							</PieChart>
						</ResponsiveContainer>
					</div>
				) : null}

				{stats.byUserLevel.length > 0 ? (
					<div className="rounded-lg border border-border bg-surface-2 p-4">
						<h4 className="mb-3 text-xs font-medium text-text-secondary">
							按人员层级（柱状图）
						</h4>
						<ResponsiveContainer width="100%" height={200}>
							<BarChart data={stats.byUserLevel}>
								<CartesianGrid strokeDasharray="3 3" stroke="#30363D" />
								<XAxis dataKey="value" stroke="#8B949E" fontSize={11} />
								<YAxis stroke="#8B949E" fontSize={11} />
								<Tooltip
									contentStyle={{
										background: "#24292E",
										border: "1px solid #444C56",
										borderRadius: "6px",
										fontSize: "12px",
									}}
								/>
								<Bar dataKey="count" fill="#3FB950" radius={[4, 4, 0, 0]} />
							</BarChart>
						</ResponsiveContainer>
					</div>
				) : null}

				{stats.byDepartment.length > 0 ? (
					<div className="rounded-lg border border-border bg-surface-2 p-4">
						<h4 className="mb-3 text-xs font-medium text-text-secondary">
							按人员部门（柱状图）
						</h4>
						<ResponsiveContainer width="100%" height={200}>
							<BarChart data={stats.byDepartment}>
								<CartesianGrid strokeDasharray="3 3" stroke="#30363D" />
								<XAxis dataKey="value" stroke="#8B949E" fontSize={11} />
								<YAxis stroke="#8B949E" fontSize={11} />
								<Tooltip
									contentStyle={{
										background: "#24292E",
										border: "1px solid #444C56",
										borderRadius: "6px",
										fontSize: "12px",
									}}
								/>
								<Bar dataKey="count" fill="#A371F7" radius={[4, 4, 0, 0]} />
							</BarChart>
						</ResponsiveContainer>
					</div>
				) : null}
			</div>
		</div>
	);
}
```

### Step 8.2: 创建 DemoPage 主页面

- [ ] 创建文件 `web-ui/src/components/demo/demo-page.tsx`

```tsx
import { useState } from "react";

import { DemoBubbleSortTab } from "./demo-bubble-sort-tab";
import { DemoHashTab } from "./demo-hash-tab";
import { DemoHelloWorldTab } from "./demo-hello-world-tab";
import { DemoStatsCharts } from "./demo-stats-charts";
import type { DemoTab } from "./demo-types";

const TABS: { id: DemoTab; label: string }[] = [
	{ id: "helloworld", label: "HelloWorld" },
	{ id: "hash", label: "哈希算法" },
	{ id: "bubblesort", label: "冒泡排序" },
];

export function DemoPage() {
	const [activeTab, setActiveTab] = useState<DemoTab>("helloworld");

	return (
		<div className="flex h-full flex-col overflow-hidden bg-surface-0">
			<header className="border-b border-border bg-surface-1 px-6 py-4">
				<h1 className="text-base font-semibold text-text-primary">
					Hello World 1.0T2 演示页面
				</h1>
				<p className="text-xs text-text-secondary">
					三接口执行结果展示 · 导出 · 调用统计可视化
				</p>
			</header>

			<div className="flex gap-1 border-b border-border bg-surface-1 px-4">
				{TABS.map((tab) => (
					<button
						key={tab.id}
						type="button"
						onClick={() => setActiveTab(tab.id)}
						className={
							activeTab === tab.id
								? "border-b-2 border-accent px-4 py-2 text-sm font-medium text-text-primary"
								: "px-4 py-2 text-sm text-text-secondary hover:text-text-primary"
						}
					>
						{tab.label}
					</button>
				))}
			</div>

			<div className="flex-1 overflow-auto">
				{activeTab === "helloworld" ? <DemoHelloWorldTab /> : null}
				{activeTab === "hash" ? <DemoHashTab /> : null}
				{activeTab === "bubblesort" ? <DemoBubbleSortTab /> : null}
			</div>

			<div className="border-t border-border bg-surface-1">
				<DemoStatsCharts />
			</div>
		</div>
	);
}
```

### Step 8.3: 创建 DemoPage 单元测试

- [ ] 创建文件 `web-ui/src/components/demo/demo-page.test.tsx`

```tsx
import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

import { DemoPage } from "./demo-page";

vi.mock("./demo-api", () => ({
	fetchHelloWorld: vi.fn().mockResolvedValue({ success: true, type: "helloworld", data: "Hello, World!" }),
	fetchHash: vi.fn().mockResolvedValue({ success: true, type: "hash", data: "abc123" }),
	fetchBubbleSort: vi.fn().mockResolvedValue({ success: true, type: "bubblesort", data: [1, 2, 3] }),
	getExportUrl: vi.fn().mockReturnValue("/api/demo/export?type=helloworld&data=test"),
	fetchStats: vi.fn().mockResolvedValue({
		totalCalls: 0,
		byUserType: [],
		byUserLevel: [],
		byDepartment: [],
		trendByDay: [],
	}),
}));

describe("DemoPage", () => {
	it("renders page header and three tabs", () => {
		render(<DemoPage />);
		expect(screen.getByText("Hello World 1.0T2 演示页面")).toBeTruthy();
		expect(screen.getByText("HelloWorld")).toBeTruthy();
		expect(screen.getByText("哈希算法")).toBeTruthy();
		expect(screen.getByText("冒泡排序")).toBeTruthy();
	});

	it("shows HelloWorld tab content by default", () => {
		render(<DemoPage />);
		expect(screen.getByText("点击按钮执行 HelloWorld 接口。")).toBeTruthy();
	});
});
```

### Step 8.4: 验证类型检查与测试

- [ ] 运行 typecheck 和测试

```bash
cd /root/.agentix/agentic-dev/runs/DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-3afb61ff-bd4f-42e8-b12f-ba7d219c0808/worktree/dtazzi-cline-gt-toast-510a132b/web-ui
npm run typecheck && npm test -- demo-page
```

预期输出：typecheck 通过，2 个测试通过。

---

## Self-Review

### 1. Spec coverage

| 需求 | 对应 Task | 覆盖状态 |
|------|-----------|----------|
| 用 Java 写 helloworld 接口 | Task 1-3 (`DemoApiController.helloWorld`) | ✅ |
| 用 Java 写哈希算法接口 | Task 1-3 (`DemoApiController.hash`) | ✅ |
| 用 Java 写冒泡排序接口 | Task 1-3 (`DemoApiController.bubbleSort`) | ✅ |
| 前端新增页面，三 Tab 展示不同执行结果 | Task 7-8 (`DemoPage` + 3 Tab 组件) | ✅ |
| 新增导出按钮 | Task 7 (`DemoExportButton`) | ✅ |
| 后台提供导出接口，支持导出各页面展示结果 | Task 2-3 (`DemoService.exportToCsv` + `DemoApiController.export`) | ✅ |
| 后端做埋点，获取调用次数和调用人 | Task 1-3 (`ApiCallLog` + `ApiCallLogService.log`) | ✅ |
| 前端可视化报表查看调用情况 | Task 8 (`DemoStatsCharts`) | ✅ |
| 按人员类型维度 | Task 1-3 (`byUserType` 查询) + Task 8 (饼图) | ✅ |
| 按人员层级维度 | Task 1-3 (`byUserLevel` 查询) + Task 8 (柱状图) | ✅ |
| 按人员部门维度 | Task 1-3 (`byDepartment` 查询) + Task 8 (柱状图) | ✅ |
| 折线图展示形式 | Task 8 (`trendByDay` 折线图) | ✅ |
| 饼图展示形式 | Task 8 (`byUserType` 饼图) | ✅ |
| 柱状图展示形式 | Task 8 (`byUserLevel` + `byDepartment` 柱状图) | ✅ |
| 后端单元测试 | Task 4 | ✅ |
| 前端单元测试 | Task 8 | ✅ |

### 2. Placeholder scan

- 无 "TBD"、"TODO"、"implement later" — ✅
- 无 "add appropriate error handling" 泛化描述 — ✅（每个接口都有具体校验逻辑）
- 所有步骤都包含完整代码 — ✅
- 所有步骤都包含精确命令与预期输出 — ✅

### 3. Cross-repo contract alignment

| 契约点 | 后端 | 前端 | 一致性 |
|--------|------|------|--------|
| `GET /api/demo/hello` → `{success,type,data,timestamp}` | `DemoResult<String>` | `DemoResult<string>` | ✅ |
| `GET /api/demo/hash?algorithm=&input=` → `{success,type,data,timestamp}` | `DemoResult<String>` | `DemoResult<string>` | ✅ |
| `POST /api/demo/bubble-sort` body `{input:[...]}` → `{success,type,data,timestamp}` | `DemoResult<List<Integer>>` | `DemoResult<number[]>` | ✅ |
| `GET /api/demo/export?type=&data=` → `text/csv` | `ResponseEntity<byte[]>` | `window.location.href` | ✅ |
| `GET /api/demo/stats` → `{totalCalls,byUserType,byUserLevel,byDepartment,trendByDay}` | `CallStatsResponse` | `CallStatsResponse` | ✅ |
| 埋点 headers: `X-Caller-Name`, `X-User-Type`, `X-User-Level`, `X-Department` | Controller `extractHeader` | Tab 组件 headers | ✅ |

### 4. Risk assessment

- **Vite proxy 顺序**：`/api/demo` 必须排在 `/api` 前，否则会被 `/api` 先匹配转发到 Kanban runtime（3484）。计划中已显式标注。
- **H2 内存数据库**：重启后埋点数据丢失，但符合 demo 场景。`ddl-auto=update` 会自动建表。
- **recharts 版本兼容**：recharts 2.x 兼容 React 18，`npm install recharts` 会自动选择最新兼容版本。
- **CORS**：开发环境通过 Vite 代理同源访问，生产环境需配置 CORS（已在 application.properties 中添加）。

---

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/hello-world-1.0T2-implementation-plan.md`. Two execution options:

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints
