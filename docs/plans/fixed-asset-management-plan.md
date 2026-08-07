# 固定资产配置管理 实施计划（Implementation Plan）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.
>
> **Goal:** 在 my-spring-boot-app 中新增「固定资产配置管理」领域模块，提供资产卡片 CRUD、分类/状态筛选与关键字搜索，不改动现有 Item/User 代码。
>
> **Architecture:** 沿用现有经典分层（Model → Repository → Service → Controller → Thymeleaf 视图）。新增 `FixedAsset` JPA 实体（表 `fixed_assets`）、`FixedAssetRepository`、`FixedAssetService`（编号唯一性双保险：应用层预检 + DB 唯一约束兜底）、`FixedAssetController`（`@RequestMapping("/assets")`），以及 `templates/assets/{list,form,view}.html`。复用 `GlobalExceptionHandler`，不新增异常类。`FixedAsset` 与 `User` 仅通过可空 `userId` 弱关联，不引入 JPA 外键。
>
> **Tech Stack:** Spring Boot 2.6.6（`spring-boot-starter-{web,data-jpa,thymeleaf,validation,test}`）、H2 内存库（`jdbc:h2:mem:itemdb`，`ddl-auto=update` 自动建表）、Java 17、JUnit 5、Thymeleaf、javax.*（非 jakarta）。
>
> **Spec (DIMA):** `.agents/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4a75d390-4551-4eb5-9f91-06505b9dd1e3/dima.md`

---

## Global Constraints

- Spring Boot 2.6.6 parent，Java 17，`javax.persistence.*` 与 `javax.validation.*`（**严禁** jakarta.*）
- H2 内存库 `jdbc:h2:mem:itemdb`，`spring.jpa.hibernate.ddl-auto=update`（已存在配置，由其自动建 `fixed_assets` 表，**不得**手写 SQL 迁移脚本）
- 校验消息一律中文，与 `Item.java` 风格一致（如「资产编号不能为空」「原值不能为负数」）
- 业务异常统一抛 `IllegalArgumentException`，由 `GlobalExceptionHandler` 兜底；控制器内对写操作自行 try/catch（与 `ItemController` 一致，避免落到全局处理器的 `redirect:/items` 兜底）
- 时间戳沿用 `@PrePersist onCreate()` / `@PreUpdate onUpdate()` 模式
- 不新增异常类、不新增字典表、不做软删除（YAGNI）
- 状态取值固定四项：`在用`、`闲置`、`维修`、`报废`
- 仅新增文件，**禁止修改** `Item`/`User`/`GlobalExceptionHandler`/`application.properties`/`pom.xml`
- 每个任务结束以 `cd my-spring-boot-app && git add -A && git commit` 频繁提交

---

## File Structure

| 职责 | 文件（新建） | 说明 |
|---|---|---|
| Model | `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java` | JPA 实体，表 `fixed_assets` |
| Repository | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/FixedAssetRepository.java` | Spring Data JPA + `@Query` 检索 |
| Service | `my-spring-boot-app/src/main/java/com/example/myapp/services/FixedAssetService.java` | `@Service @Transactional` + 编号唯一性校验 |
| Controller | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java` | `@RequestMapping("/assets")` + 状态字典 |
| View | `my-spring-boot-app/src/main/resources/templates/assets/list.html` | 列表 + 分类/状态筛选 |
| View | `my-spring-boot-app/src/main/resources/templates/assets/form.html` | 新增/编辑表单 |
| View | `my-spring-boot-app/src/main/resources/templates/assets/view.html` | 详情 |
| Test | `my-spring-boot-app/src/test/java/com/example/myapp/services/FixedAssetServiceTest.java` | Service 层 CRUD/唯一性/检索/删除异常 |
| Test | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/FixedAssetControllerTest.java` | Controller 层校验回显/重定向/详情 |

**关键既有参考文件（只读，不得修改，供实现者对照模式）：**
- `my-spring-boot-app/src/main/java/com/example/myapp/models/Item.java`（实体/时间戳/校验消息风格）
- `my-spring-boot-app/src/main/java/com/example/myapp/repositories/ItemRepository.java`（`@Lock`/`@Query`/`findAllCategories` 模式）
- `my-spring-boot-app/src/main/java/com/example/myapp/services/ItemService.java`（save/update 唯一性双保险模式）
- `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ItemController.java`（端点 + flash 重定向 + try/catch 模式）
- `my-spring-boot-app/src/main/java/com/example/myapp/exception/GlobalExceptionHandler.java`（兜底重定向到 `/items`，故资产控制器须自 catch）
- `my-spring-boot-app/src/main/resources/templates/items/{list,form,view}.html`（Thymeleaf 布局/flash/内联错误风格）
- `my-spring-boot-app/src/test/java/com/example/myapp/MyAppApplicationTests.java`（现有唯一测试，`@SpringBootTest` 风格）

> **实现者注意（零上下文须知）：** 现仓库**没有** `ItemServiceTest`/`ItemControllerTest`，Service 层测试是仓库首个非冒烟测试。本计划 Task 1 建立该测试基线（`@SpringBootTest` + H2 + `@Transactional` 回滚隔离）。

---

## Task 1: FixedAsset 实体 + Repository + Service 冒烟测试基线

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java`
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/repositories/FixedAssetRepository.java`
- Create: `my-spring-boot-app/src/test/java/com/example/myapp/services/FixedAssetServiceTest.java`（本任务仅放「保存可持久化」冒烟用例，后续 Task 2 扩充）
- Test: 同上

**Interfaces:**
- Produces: `FixedAsset` 实体（全部字段 + getter/setter + `@PrePersist`/`@PreUpdate`），`FixedAssetRepository extends JpaRepository<FixedAsset, Long>`（本任务含 `existsByAssetNo`、`findByAssetNoForUpdate`、`searchByKeyword`、`findByCategory`、`findByStatus`、`findAllCategories`，供 Task 2/3 使用）

**Context:** DIMA §3、§4、§5.3、§5.4。`ddl-auto=update` 负责建表；实体只声明注解即可。

### Steps

- [ ] 1.1 创建 `FixedAsset.java`，内容如下（逐字录入，勿改字段名/校验消息）：

```java
package com.example.myapp.models;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixed_assets")
public class FixedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "资产编号不能为空")
    @Size(min = 1, max = 50, message = "资产编号长度必须在1-50之间")
    @Column(nullable = false, unique = true, length = 50)
    private String assetNo;

    @NotBlank(message = "资产名称不能为空")
    @Size(min = 1, max = 100, message = "资产名称长度必须在1-100之间")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "资产分类不能为空")
    @Size(max = 50, message = "资产分类长度不能超过50")
    @Column(nullable = false, length = 50)
    private String category;

    @Size(max = 200, message = "规格型号长度不能超过200")
    @Column(length = 200)
    private String spec;

    @NotBlank(message = "资产状态不能为空")
    @Size(max = 20, message = "资产状态长度不能超过20")
    @Column(nullable = false, length = 20)
    private String status;

    @NotNull(message = "原值不能为空")
    @DecimalMin(value = "0.0", inclusive = true, message = "原值不能为负数")
    @Digits(integer = 10, fraction = 2, message = "原值格式不正确")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal originalValue;

    @Column(name = "user_id")
    private Long userId;

    @NotNull(message = "购置日期不能为空")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Size(max = 100, message = "存放地点长度不能超过100")
    @Column(length = 100)
    private String location;

    @Size(max = 500, message = "备注长度不能超过500")
    @Column(length = 500)
    private String remark;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public FixedAsset() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(BigDecimal originalValue) {
        this.originalValue = originalValue;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

- [ ] 1.2 创建 `FixedAssetRepository.java`，内容如下：

```java
package com.example.myapp.repositories;

import com.example.myapp.models.FixedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface FixedAssetRepository extends JpaRepository<FixedAsset, Long> {

    Optional<FixedAsset> findByAssetNo(String assetNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM FixedAsset a WHERE a.assetNo = :assetNo")
    Optional<FixedAsset> findByAssetNoForUpdate(@Param("assetNo") String assetNo);

    boolean existsByAssetNo(String assetNo);

    List<FixedAsset> findByCategory(String category);

    List<FixedAsset> findByStatus(String status);

    @Query("SELECT a FROM FixedAsset a WHERE " +
           "LOWER(a.assetNo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.spec) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FixedAsset> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT a.category FROM FixedAsset a ORDER BY a.category")
    List<String> findAllCategories();
}
```

- [ ] 1.3 创建 `FixedAssetServiceTest.java`，先写「保存可持久化」冒烟测试（建立仓库首个 service 测试基线）。内容如下（本任务只保留这一个用例，Task 2 会追加其余用例）：

```java
package com.example.myapp.services;

import com.example.myapp.models.FixedAsset;
import com.example.myapp.repositories.FixedAssetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FixedAssetServiceTest {

    @Autowired
    private FixedAssetService fixedAssetService;

    @Autowired
    private FixedAssetRepository fixedAssetRepository;

    private FixedAsset sampleAsset() {
        FixedAsset a = new FixedAsset();
        a.setAssetNo("FA-001");
        a.setName("测试笔记本电脑");
        a.setCategory("电子设备");
        a.setSpec("14寸 i7 16G");
        a.setStatus("在用");
        a.setOriginalValue(new BigDecimal("8888.00"));
        a.setPurchaseDate(LocalDate.of(2026, 8, 7));
        a.setLocation("A座3楼");
        a.setRemark("研发用机");
        return a;
    }

    @Test
    void save_persistsAssetAndFillsTimestamps() {
        FixedAsset saved = fixedAssetService.save(sampleAsset());

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals("FA-001", saved.getAssetNo());
    }
}
```

> 说明：此时 `FixedAssetService` 尚不存在，测试必然编译失败——这是 TDD 的红灯。

- [ ] 1.4 运行测试，确认它因 `FixedAssetService` 缺失而编译失败（红灯预期）：

```bash
cd my-spring-boot-app && ./mvnw -q test -Dtest=FixedAssetServiceTest 2>&1 | tail -20
```

预期输出包含 `COMPILATION ERROR` 或 `cannot find symbol class FixedAssetService`。这是 TDD 红灯，继续下一步。

- [ ] 1.5 创建 `FixedAssetService.java` 最小实现（仅让冒烟测试通过；唯一性/检索逻辑在 Task 2 以 TDD 补全）。内容如下：

```java
package com.example.myapp.services;

import com.example.myapp.models.FixedAsset;
import com.example.myapp.repositories.FixedAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FixedAssetService {

    private final FixedAssetRepository fixedAssetRepository;

    @Autowired
    public FixedAssetService(FixedAssetRepository fixedAssetRepository) {
        this.fixedAssetRepository = fixedAssetRepository;
    }

    public List<FixedAsset> findAll() {
        return fixedAssetRepository.findAll();
    }

    public Optional<FixedAsset> findById(Long id) {
        return fixedAssetRepository.findById(id);
    }

    public FixedAsset save(FixedAsset asset) {
        return fixedAssetRepository.save(asset);
    }
}
```

- [ ] 1.6 运行冒烟测试，确认通过（绿灯）：

```bash
cd my-spring-boot-app && ./mvnw -q test -Dtest=FixedAssetServiceTest 2>&1 | tail -20
```

预期：`Tests run: 1, Failures: 0, Errors: 0`。

- [ ] 1.7 提交：

```bash
cd my-spring-boot-app && git add -A && git commit -m "feat(asset): 新增 FixedAsset 实体/仓储/Service 冒烟基线"
```

---

## Task 2: FixedAssetService 完整业务逻辑（唯一性 / 更新 / 检索 / 删除异常）

**Files:**
- Modify: `my-spring-boot-app/src/main/java/com/example/myapp/services/FixedAssetService.java`
- Test: `my-spring-boot-app/src/test/java/com/example/myapp/services/FixedAssetServiceTest.java`

**Interfaces:**
- Consumes: `FixedAssetRepository`（Task 1 产出的 `existsByAssetNo`/`findByAssetNoForUpdate`/`searchByKeyword`/`findByCategory`/`findByStatus`/`findAllCategories`/`existsById`）
- Produces: `save`(带唯一性校验)、`update(Long, FixedAsset)`、`deleteById(Long)`、`searchByKeyword(String)`、`findByCategory(String)`、`findByStatus(String)`、`getAllCategories()`

**Context:** DIMA §5.1（编号唯一性双保险）、§5.3（分类聚合）、§5.4（检索回退）、§7（删除/不存在异常）。复用 `ItemService.save`/`update` 模式。

### Steps

- [ ] 2.1 在 `FixedAssetServiceTest.java` 追加（保留 Task 1 的 `save_persistsAssetAndFailsTimestamps`）以下用例。完整文件应为：

```java
package com.example.myapp.services;

import com.example.myapp.models.FixedAsset;
import com.example.myapp.repositories.FixedAssetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FixedAssetServiceTest {

    @Autowired
    private FixedAssetService fixedAssetService;

    @Autowired
    private FixedAssetRepository fixedAssetRepository;

    private FixedAsset sampleAsset() {
        FixedAsset a = new FixedAsset();
        a.setAssetNo("FA-001");
        a.setName("测试笔记本电脑");
        a.setCategory("电子设备");
        a.setSpec("14寸 i7 16G");
        a.setStatus("在用");
        a.setOriginalValue(new BigDecimal("8888.00"));
        a.setPurchaseDate(LocalDate.of(2026, 8, 7));
        a.setLocation("A座3楼");
        a.setRemark("研发用机");
        return a;
    }

    private FixedAsset sampleAsset(String assetNo, String name) {
        FixedAsset a = sampleAsset();
        a.setAssetNo(assetNo);
        a.setName(name);
        return a;
    }

    @Test
    void save_persistsAssetAndFillsTimestamps() {
        FixedAsset saved = fixedAssetService.save(sampleAsset());

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals("FA-001", saved.getAssetNo());
    }

    @Test
    void save_duplicateAssetNo_throwsIllegalArgument() {
        fixedAssetService.save(sampleAsset("FA-DUP", "第一台"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fixedAssetService.save(sampleAsset("FA-DUP", "第二台同名编号")));
        assertTrue(ex.getMessage().contains("FA-DUP"));
        assertTrue(ex.getMessage().contains("已存在"));
    }

    @Test
    void update_changesFieldsAndKeepsId() {
        FixedAsset saved = fixedAssetService.save(sampleAsset("FA-UP", "更新前名称"));
        FixedAsset details = sampleAsset("FA-UP", "更新后名称");
        details.setCategory("办公设备");
        details.setStatus("闲置");

        FixedAsset updated = fixedAssetService.update(saved.getId(), details);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("更新后名称", updated.getName());
        assertEquals("办公设备", updated.getCategory());
        assertEquals("闲置", updated.getStatus());
    }

    @Test
    void update_toExistingAssetNo_throwsIllegalArgument() {
        fixedAssetService.save(sampleAsset("FA-A", "A 资产"));
        FixedAsset target = fixedAssetService.save(sampleAsset("FA-B", "B 资产"));

        FixedAsset details = sampleAsset("FA-A", "想改成 A 的编号");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fixedAssetService.update(target.getId(), details));
        assertTrue(ex.getMessage().contains("FA-A"));
        assertTrue(ex.getMessage().contains("已存在"));
    }

    @Test
    void update_nonexistentId_throwsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fixedAssetService.update(99999L, sampleAsset("FA-X", "不存在")));
        assertTrue(ex.getMessage().contains("固定资产不存在"));
    }

    @Test
    void deleteById_nonexistent_throwsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fixedAssetService.deleteById(99999L));
        assertTrue(ex.getMessage().contains("固定资产不存在"));
    }

    @Test
    void deleteById_existing_removesRecord() {
        FixedAsset saved = fixedAssetService.save(sampleAsset("FA-DEL", "待删除"));
        fixedAssetService.deleteById(saved.getId());
        assertFalse(fixedAssetRepository.existsById(saved.getId()));
    }

    @Test
    void searchByKeyword_matchesAssetNoOrNameOrSpec() {
        fixedAssetService.save(sampleAsset("FA-K1", "投影仪"));
        fixedAssetService.save(sampleAsset("FA-K2", "白板笔"));

        List<FixedAsset> hit = fixedAssetService.searchByKeyword("FA-K1");
        assertEquals(1, hit.size());

        List<FixedAsset> byName = fixedAssetService.searchByKeyword("投影");
        assertEquals(1, byName.size());
    }

    @Test
    void searchByKeyword_blank_returnsAll() {
        fixedAssetService.save(sampleAsset("FA-K3", "第一"));
        fixedAssetService.save(sampleAsset("FA-K4", "第二"));

        List<FixedAsset> all = fixedAssetService.searchByKeyword("   ");
        assertTrue(all.size() >= 2);
    }

    @Test
    void findByCategory_andFindByStatus_filterCorrectly() {
        fixedAssetService.save(sampleAsset("FA-C1", "设备1"));
        FixedAsset two = sampleAsset("FA-C2", "设备2");
        two.setStatus("闲置");
        two.setCategory("办公设备");
        fixedAssetService.save(two);

        assertEquals(1, fixedAssetService.findByCategory("办公设备").size());
        assertEquals(1, fixedAssetService.findByStatus("闲置").size());
    }

    @Test
    void getAllCategories_returnsDistinctSorted() {
        fixedAssetService.save(sampleAsset("FA-G1", "a"));
        FixedAsset b = sampleAsset("FA-G2", "b");
        b.setCategory("电子设备");
        fixedAssetService.save(b);

        List<String> cats = fixedAssetService.getAllCategories();
        assertTrue(cats.contains("电子设备"));
        assertEquals(cats.stream().distinct().count(), cats.size());
    }
}
```

- [ ] 2.2 运行测试，确认全部失败（红灯——`update`/`deleteById`/`searchByKeyword` 等方法尚未实现）：

```bash
cd my-spring-boot-app && ./mvnw -q test -Dtest=FixedAssetServiceTest 2>&1 | tail -30
```

预期：编译失败（`update`/`deleteById` 等方法找不到）或多个用例红。继续。

- [ ] 2.3 将 `FixedAssetService.java` 替换为完整实现（逐字录入）：

```java
package com.example.myapp.services;

import com.example.myapp.models.FixedAsset;
import com.example.myapp.repositories.FixedAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FixedAssetService {

    private final FixedAssetRepository fixedAssetRepository;

    @Autowired
    public FixedAssetService(FixedAssetRepository fixedAssetRepository) {
        this.fixedAssetRepository = fixedAssetRepository;
    }

    public List<FixedAsset> findAll() {
        return fixedAssetRepository.findAll();
    }

    public Optional<FixedAsset> findById(Long id) {
        return fixedAssetRepository.findById(id);
    }

    public FixedAsset save(FixedAsset asset) {
        try {
            if (asset.getId() == null && fixedAssetRepository.existsByAssetNo(asset.getAssetNo())) {
                throw new IllegalArgumentException("资产编号 '" + asset.getAssetNo() + "' 已存在");
            }
            return fixedAssetRepository.save(asset);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("资产编号 '" + asset.getAssetNo() + "' 已存在", e);
        }
    }

    public FixedAsset update(Long id, FixedAsset details) {
        FixedAsset asset = fixedAssetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("固定资产不存在，ID: " + id));

        try {
            if (!asset.getAssetNo().equals(details.getAssetNo())) {
                fixedAssetRepository.findByAssetNoForUpdate(details.getAssetNo())
                        .ifPresent(existing -> {
                            throw new IllegalArgumentException("资产编号 '" + details.getAssetNo() + "' 已存在");
                        });
            }

            asset.setAssetNo(details.getAssetNo());
            asset.setName(details.getName());
            asset.setCategory(details.getCategory());
            asset.setSpec(details.getSpec());
            asset.setStatus(details.getStatus());
            asset.setOriginalValue(details.getOriginalValue());
            asset.setUserId(details.getUserId());
            asset.setPurchaseDate(details.getPurchaseDate());
            asset.setLocation(details.getLocation());
            asset.setRemark(details.getRemark());

            return fixedAssetRepository.save(asset);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("资产编号 '" + details.getAssetNo() + "' 已存在", e);
        }
    }

    public void deleteById(Long id) {
        if (!fixedAssetRepository.existsById(id)) {
            throw new IllegalArgumentException("固定资产不存在，ID: " + id);
        }
        fixedAssetRepository.deleteById(id);
    }

    public List<FixedAsset> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return fixedAssetRepository.searchByKeyword(keyword.trim());
    }

    public List<FixedAsset> findByCategory(String category) {
        return fixedAssetRepository.findByCategory(category);
    }

    public List<FixedAsset> findByStatus(String status) {
        return fixedAssetRepository.findByStatus(status);
    }

    public List<String> getAllCategories() {
        return fixedAssetRepository.findAllCategories();
    }
}
```

- [ ] 2.4 运行测试，确认全绿：

```bash
cd my-spring-boot-app && ./mvnw -q test -Dtest=FixedAssetServiceTest 2>&1 | tail -30
```

预期：`Tests run: 12, Failures: 0, Errors: 0`（含 Task 1 冒烟用例）。

- [ ] 2.5 提交：

```bash
cd my-spring-boot-app && git add -A && git commit -m "feat(asset): Service 编号唯一性双保险/更新/检索/删除异常"
```

---

## Task 3: FixedAssetController（端点 + 状态字典 + flash 重定向）

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java`
- Test: `my-spring-boot-app/src/test/java/com/example/myapp/controllers/FixedAssetControllerTest.java`

**Interfaces:**
- Consumes: `FixedAssetService`（Task 2 产出：`findAll`/`findById`/`save`/`update`/`deleteById`/`searchByKeyword`/`findByCategory`/`findByStatus`/`getAllCategories`）
- Produces: `@Controller @RequestMapping("/assets")`，暴露 10 个端点 + `getAllStatuses()` 状态字典

**Context:** DIMA §5.2（状态字典硬编码四项）、§6（端点表）、§7（控制器内 try/catch `IllegalArgumentException` + flash 重定向，避免落到 `GlobalExceptionHandler` 的 `redirect:/items` 兜底）。复用 `ItemController` 模式。

### Steps

- [ ] 3.1 创建 `FixedAssetControllerTest.java`（`@WebMvcTest` + `@MockBean` service；Controller 尚不存在，红灯预期）。内容如下：

```java
package com.example.myapp.controllers;

import com.example.myapp.models.FixedAsset;
import com.example.myapp.services.FixedAssetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FixedAssetController.class)
class FixedAssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FixedAssetService fixedAssetService;

    private FixedAsset sampleAsset(Long id) {
        FixedAsset a = new FixedAsset();
        a.setId(id);
        a.setAssetNo("FA-001");
        a.setName("测试电脑");
        a.setCategory("电子设备");
        a.setStatus("在用");
        a.setOriginalValue(new BigDecimal("8888.00"));
        a.setPurchaseDate(LocalDate.of(2026, 8, 7));
        return a;
    }

    @Test
    void list_returnsListView() throws Exception {
        when(fixedAssetService.findAll()).thenReturn(Collections.emptyList());
        when(fixedAssetService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(view().name("assets/list"));
    }

    @Test
    void newForm_returnsFormViewWithEmptyAsset() throws Exception {
        mockMvc.perform(get("/assets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("assets/form"))
                .andExpect(model().attributeExists("asset"));
    }

    @Test
    void create_withInvalidForm_returnsFormView() throws Exception {
        // 缺 assetNo、name 等必填 -> 校验失败回表单
        mockMvc.perform(post("/assets")
                        .param("category", "电子设备")
                        .param("status", "在用")
                        .param("originalValue", "100.00")
                        .param("purchaseDate", "2026-08-07"))
                .andExpect(status().isOk())
                .andExpect(view().name("assets/form"));
    }

    @Test
    void create_validForm_redirectsToList() throws Exception {
        when(fixedAssetService.save(any(FixedAsset.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/assets")
                        .param("assetNo", "FA-001")
                        .param("name", "测试电脑")
                        .param("category", "电子设备")
                        .param("status", "在用")
                        .param("originalValue", "8888.00")
                        .param("purchaseDate", "2026-08-07"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assets"));
    }

    @Test
    void create_duplicateAssetNo_redirectsToNewForm() throws Exception {
        when(fixedAssetService.save(any(FixedAsset.class)))
                .thenThrow(new IllegalArgumentException("资产编号 'FA-001' 已存在"));

        mockMvc.perform(post("/assets")
                        .param("assetNo", "FA-001")
                        .param("name", "测试电脑")
                        .param("category", "电子设备")
                        .param("status", "在用")
                        .param("originalValue", "8888.00")
                        .param("purchaseDate", "2026-08-07"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assets/new"));
    }

    @Test
    void view_existing_returnsDetailView() throws Exception {
        when(fixedAssetService.findById(1L)).thenReturn(Optional.of(sampleAsset(1L)));

        mockMvc.perform(get("/assets/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("assets/view"))
                .andExpect(model().attributeExists("asset"));
    }

    @Test
    void view_nonexistent_redirectsToList() throws Exception {
        when(fixedAssetService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/assets/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assets"));
    }

    @Test
    void search_returnsListViewWithKeyword() throws Exception {
        when(fixedAssetService.searchByKeyword("电脑")).thenReturn(Collections.emptyList());
        when(fixedAssetService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/assets/search").param("keyword", "电脑"))
                .andExpect(status().isOk())
                .andExpect(view().name("assets/list"))
                .andExpect(model().attributeExists("keyword"));
    }

    @Test
    void statusFilter_returnsListView() throws Exception {
        when(fixedAssetService.findByStatus("在用")).thenReturn(Collections.emptyList());
        when(fixedAssetService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/assets/status/在用"))
                .andExpect(status().isOk())
                .andExpect(view().name("assets/list"));
    }
}
```

- [ ] 3.2 运行，确认编译失败（Controller 不存在，红灯）：

```bash
cd my-spring-boot-app && ./mvnw -q test -Dtest=FixedAssetControllerTest 2>&1 | tail -20
```

- [ ] 3.3 创建 `FixedAssetController.java`（逐字录入；注意写操作 try/catch + flash，与 `ItemController` 一致）：

```java
package com.example.myapp.controllers;

import com.example.myapp.models.FixedAsset;
import com.example.myapp.services.FixedAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/assets")
public class FixedAssetController {

    private static final List<String> STATUSES = Arrays.asList("在用", "闲置", "维修", "报废");

    private final FixedAssetService fixedAssetService;

    @Autowired
    public FixedAssetController(FixedAssetService fixedAssetService) {
        this.fixedAssetService = fixedAssetService;
    }

    @GetMapping
    public String listAssets(Model model) {
        model.addAttribute("assets", fixedAssetService.findAll());
        model.addAttribute("categories", fixedAssetService.getAllCategories());
        model.addAttribute("statuses", getAllStatuses());
        return "assets/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("asset", new FixedAsset());
        model.addAttribute("statuses", getAllStatuses());
        return "assets/form";
    }

    @PostMapping
    public String createAsset(@Valid @ModelAttribute FixedAsset asset,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "assets/form";
        }
        try {
            fixedAssetService.save(asset);
            redirectAttributes.addFlashAttribute("success", "固定资产创建成功！");
            return "redirect:/assets";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/assets/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            FixedAsset asset = fixedAssetService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("固定资产不存在，ID: " + id));
            model.addAttribute("asset", asset);
            model.addAttribute("statuses", getAllStatuses());
            return "assets/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/assets";
        }
    }

    @PostMapping("/{id}")
    public String updateAsset(@PathVariable Long id,
                             @Valid @ModelAttribute FixedAsset asset,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "assets/form";
        }
        try {
            fixedAssetService.update(id, asset);
            redirectAttributes.addFlashAttribute("success", "固定资产更新成功！");
            return "redirect:/assets";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/assets/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAsset(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            fixedAssetService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "固定资产删除成功！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/assets";
    }

    @GetMapping("/search")
    public String searchAssets(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("assets", fixedAssetService.searchByKeyword(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", fixedAssetService.getAllCategories());
        model.addAttribute("statuses", getAllStatuses());
        return "assets/list";
    }

    @GetMapping("/category/{category}")
    public String getAssetsByCategory(@PathVariable String category, Model model) {
        model.addAttribute("assets", fixedAssetService.findByCategory(category));
        model.addAttribute("category", category);
        model.addAttribute("categories", fixedAssetService.getAllCategories());
        model.addAttribute("statuses", getAllStatuses());
        return "assets/list";
    }

    @GetMapping("/status/{status}")
    public String getAssetsByStatus(@PathVariable String status, Model model) {
        model.addAttribute("assets", fixedAssetService.findByStatus(status));
        model.addAttribute("statusFilter", status);
        model.addAttribute("categories", fixedAssetService.getAllCategories());
        model.addAttribute("statuses", getAllStatuses());
        return "assets/list";
    }

    @GetMapping("/{id}")
    public String viewAsset(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            FixedAsset asset = fixedAssetService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("固定资产不存在，ID: " + id));
            model.addAttribute("asset", asset);
            return "assets/view";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/assets";
        }
    }

    public List<String> getAllStatuses() {
        return STATUSES;
    }
}
```

- [ ] 3.4 运行 Controller 测试。注意：测试断言 `view() = "assets/form"`，但视图文件 Task 4 才创建——`@WebMvcTest` 不加载 Thymeleaf 模板渲染，断言只校验 view name 与 model，故 Task 3 阶段应能通过。运行：

```bash
cd my-spring-boot-app && ./mvnw -q test -Dtest=FixedAssetControllerTest 2>&1 | tail -30
```

预期：`Tests run: 9, Failures: 0, Errors: 0`。若 `create_invalidForm_returnsFormView` 因字段绑定失败计数不符，可微调断言，但不得放宽必填校验。

- [ ] 3.5 提交：

```bash
cd my-spring-boot-app && git add -A && git commit -m "feat(asset): FixedAssetController 端点/状态字典/flash 重定向"
```

---

## Task 4: Thymeleaf 视图（list / form / view）

**Files:**
- Create: `my-spring-boot-app/src/main/resources/templates/assets/list.html`
- Create: `my-spring-boot-app/src/main/resources/templates/assets/form.html`
- Create: `my-spring-boot-app/src/main/resources/templates/assets/view.html`

**Interfaces:**
- Consumes: Controller 产出的 model 属性：`assets`(List<FixedAsset>)、`asset`(FixedAsset)、`keyword`、`category`、`statusFilter`、`categories`(List<String>)、`statuses`(List<String>)、flash `success`/`error`
- Produces: 三个 Thymeleaf 模板，复用 `items/*` 布局风格

**Context:** DIMA §6（视图复用 items 模板布局与 flash 约定）。表单校验失败回 `assets/form` 并保留输入、内联展示字段错误。

### Steps

- [ ] 4.1 创建 `list.html`（逐字录入；含分类/状态筛选链接 + flash 提示 + 关键字搜索）：

```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>固定资产配置管理</title>
    <style>
        :root {
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --primary: #0f766e;
            --danger: #dc2626;
            --border: #e5e7eb;
        }
        * { box-sizing: border-box; }
        body {
            margin: 0;
            font-family: "Helvetica Neue", "PingFang SC", sans-serif;
            background: linear-gradient(135deg, #eef7f4, #f7fafc);
            color: var(--text);
        }
        .container {
            max-width: 1100px;
            margin: 40px auto;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
        }
        .top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; gap: 16px; }
        h1 { margin: 0; font-size: 26px; }
        .desc { margin-top: 6px; color: var(--muted); font-size: 14px; }
        .btn { display: inline-block; border: none; border-radius: 8px; padding: 10px 14px; text-decoration: none; font-size: 14px; cursor: pointer; }
        .btn-primary { background: var(--primary); color: #fff; }
        .btn-danger { background: #fee2e2; color: var(--danger); }
        .btn-secondary { background: #e2e8f0; color: #334155; }
        .alert { padding: 12px; border-radius: 8px; margin-bottom: 16px; font-size: 14px; }
        .alert-success { background: #ecfdf5; color: #065f46; border: 1px solid #a7f3d0; }
        .alert-error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        .search-section { margin-bottom: 20px; padding: 16px; background: #f8fafc; border-radius: 8px; border: 1px solid var(--border); }
        .search-form { display: flex; gap: 10px; align-items: center; }
        .search-input { flex: 1; border: 1px solid var(--border); border-radius: 8px; padding: 10px; font-size: 14px; }
        .filter-section { margin-bottom: 20px; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
        .filter-label { font-size: 14px; color: var(--muted); font-weight: 500; margin-right: 4px; }
        .filter-link { padding: 6px 12px; border-radius: 6px; text-decoration: none; color: var(--muted); font-size: 14px; background: #f1f5f9; transition: all 0.2s; }
        .filter-link:hover { background: #e2e8f0; }
        .filter-link.active { background: var(--primary); color: #fff; }
        table { width: 100%; border-collapse: collapse; border-radius: 10px; border: 1px solid var(--border); overflow: hidden; }
        th, td { padding: 12px; border-bottom: 1px solid var(--border); text-align: left; font-size: 14px; }
        th { background: #f8fafc; }
        tr:hover td { background: #f9fcff; }
        .actions { display: flex; gap: 8px; align-items: center; }
        .empty { margin: 16px 0; color: var(--muted); }
        .item-link { color: var(--primary); text-decoration: none; font-weight: 500; }
        .item-link:hover { text-decoration: underline; }
        @media (max-width: 700px) { .container { margin: 16px; padding: 16px; } th:nth-child(3), td:nth-child(3) { display: none; } }
    </style>
</head>
<body>
<div class="container">
    <div class="top">
        <div>
            <h1>固定资产配置管理</h1>
            <div class="desc">资产卡片登记、分类与状态筛选、关键字检索</div>
        </div>
        <div style="display:flex;gap:10px;align-items:center;">
            <a class="btn btn-secondary" th:href="@{/items}">物品管理</a>
            <a class="btn btn-primary" th:href="@{/assets/new}">+ 新增资产</a>
        </div>
    </div>

    <div class="alert alert-success" th:if="${not #lists.isEmpty(success)}" th:text="${success}"></div>
    <div class="alert alert-error" th:if="${not #lists.isEmpty(error)}" th:text="${error}"></div>

    <div class="search-section">
        <form th:action="@{/assets/search}" method="get" class="search-form">
            <input type="text" name="keyword" placeholder="搜索资产编号、名称、规格..." class="search-input" th:value="${keyword}">
            <button type="submit" class="btn btn-primary">搜索</button>
            <a th:href="@{/assets}" class="btn btn-secondary">重置</a>
        </form>
    </div>

    <div class="filter-section" th:if="${not #lists.isEmpty(categories)}">
        <span class="filter-label">分类:</span>
        <a th:href="@{/assets}" class="filter-link" th:classappend="${category == null ? ' active' : ''}">全部</a>
        <a th:each="c : ${categories}" th:href="@{/assets/category/{c}(c=${c})}" class="filter-link" th:classappend="${category == c ? ' active' : ''}" th:text="${c}"></a>
    </div>

    <div class="filter-section" th:if="${not #lists.isEmpty(statuses)}">
        <span class="filter-label">状态:</span>
        <a th:href="@{/assets}" class="filter-link" th:classappend="${statusFilter == null ? ' active' : ''}">全部</a>
        <a th:each="s : ${statuses}" th:href="@{/assets/status/{s}(s=${s})}" class="filter-link" th:classappend="${statusFilter == s ? ' active' : ''}" th:text="${s}"></a>
    </div>

    <p class="empty" th:if="${#lists.isEmpty(assets)}">当前没有固定资产，请先创建一条记录。</p>

    <table th:if="${not #lists.isEmpty(assets)}">
        <thead>
        <tr>
            <th>ID</th>
            <th>资产编号</th>
            <th>名称</th>
            <th>分类</th>
            <th>状态</th>
            <th>原值</th>
            <th>存放地点</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="a : ${assets}">
            <td th:text="${a.id}"></td>
            <td th:text="${a.assetNo}"></td>
            <td>
                <a th:href="@{/assets/{id}(id=${a.id})}" class="item-link" th:text="${a.name}"></a>
            </td>
            <td th:text="${a.category}"></td>
            <td th:text="${a.status}"></td>
            <td th:text="${#numbers.formatDecimal(a.originalValue, 1, 2)}"></td>
            <td th:text="${a.location}"></td>
            <td>
                <div class="actions">
                    <a class="btn btn-secondary" th:href="@{/assets/{id}/edit(id=${a.id})}">编辑</a>
                    <form th:action="@{/assets/{id}/delete(id=${a.id})}" method="post" style="display:inline;">
                        <button type="submit" class="btn btn-danger">删除</button>
                    </form>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>
```

- [ ] 4.2 创建 `form.html`（逐字录入；含 `th:errors` 内联字段错误 + 状态下拉）：

```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:text="${asset.id == null ? '新增固定资产' : '编辑固定资产'}"></title>
    <style>
        :root { --card: #fff; --text: #1f2937; --muted: #6b7280; --primary: #0f766e; --border: #e5e7eb; --danger: #dc2626; }
        * { box-sizing: border-box; }
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: linear-gradient(135deg, #f0fdf4, #eff6ff); color: var(--text); }
        .container { max-width: 720px; margin: 40px auto; background: var(--card); border: 1px solid var(--border); border-radius: 12px; padding: 24px; box-shadow: 0 10px 30px rgba(15,23,42,0.06); }
        h1 { margin-top: 0; margin-bottom: 18px; }
        .form-row { margin-bottom: 14px; }
        label { display: block; margin-bottom: 6px; font-size: 14px; color: var(--muted); }
        input, select, textarea { width: 100%; border: 1px solid var(--border); border-radius: 8px; padding: 10px; font-size: 14px; }
        .field-error { color: var(--danger); font-size: 13px; margin-top: 4px; }
        .actions { margin-top: 20px; display: flex; gap: 10px; align-items: center; }
        .btn { border: none; border-radius: 8px; padding: 10px 14px; text-decoration: none; font-size: 14px; cursor: pointer; }
        .btn-primary { background: var(--primary); color: #fff; }
        .btn-secondary { background: #e2e8f0; color: #334155; }
        .row-2 { display: flex; gap: 14px; }
        .row-2 > div { flex: 1; }
    </style>
</head>
<body>
<div class="container">
    <h1 th:text="${asset.id == null ? '新增固定资产' : '编辑固定资产'}"></h1>

    <form th:object="${asset}" th:action="${asset.id == null ? '/assets' : '/assets/' + asset.id}" method="post">
        <div class="row-2">
            <div class="form-row">
                <label for="assetNo">资产编号</label>
                <input id="assetNo" type="text" th:field="*{assetNo}" placeholder="如 FA-001" required>
                <div class="field-error" th:if="${#fields.hasErrors('assetNo')}" th:errors="*{assetNo}"></div>
            </div>
            <div class="form-row">
                <label for="name">资产名称</label>
                <input id="name" type="text" th:field="*{name}" placeholder="资产名称" required>
                <div class="field-error" th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></div>
            </div>
        </div>

        <div class="row-2">
            <div class="form-row">
                <label for="category">分类</label>
                <input id="category" type="text" th:field="*{category}" placeholder="如 电子设备、办公设备" required>
                <div class="field-error" th:if="${#fields.hasErrors('category')}" th:errors="*{category}"></div>
            </div>
            <div class="form-row">
                <label for="status">状态</label>
                <select id="status" th:field="*{status}">
                    <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}"></option>
                </select>
                <div class="field-error" th:if="${#fields.hasErrors('status')}" th:errors="*{status}"></div>
            </div>
        </div>

        <div class="row-2">
            <div class="form-row">
                <label for="spec">规格型号</label>
                <input id="spec" type="text" th:field="*{spec}" placeholder="可选">
                <div class="field-error" th:if="${#fields.hasErrors('spec')}" th:errors="*{spec}"></div>
            </div>
            <div class="form-row">
                <label for="originalValue">原值（元）</label>
                <input id="originalValue" type="number" min="0" step="0.01" th:field="*{originalValue}" required>
                <div class="field-error" th:if="${#fields.hasErrors('originalValue')}" th:errors="*{originalValue}"></div>
            </div>
        </div>

        <div class="row-2">
            <div class="form-row">
                <label for="purchaseDate">购置日期</label>
                <input id="purchaseDate" type="date" th:field="*{purchaseDate}" required>
                <div class="field-error" th:if="${#fields.hasErrors('purchaseDate')}" th:errors="*{purchaseDate}"></div>
            </div>
            <div class="form-row">
                <label for="location">存放地点</label>
                <input id="location" type="text" th:field="*{location}" placeholder="可选">
                <div class="field-error" th:if="${#fields.hasErrors('location')}" th:errors="*{location}"></div>
            </div>
        </div>

        <div class="form-row">
            <label for="remark">备注</label>
            <textarea id="remark" th:field="*{remark}" rows="3" placeholder="可选"></textarea>
            <div class="field-error" th:if="${#fields.hasErrors('remark')}" th:errors="*{remark}"></div>
        </div>

        <div class="actions">
            <button class="btn btn-primary" type="submit">保存</button>
            <a class="btn btn-secondary" th:href="@{/assets}">返回列表</a>
        </div>
    </form>
</div>
</body>
</html>
```

- [ ] 4.3 创建 `view.html`（逐字录入）：

```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>固定资产详情</title>
    <style>
        :root { --card: #fff; --text: #1f2937; --muted: #6b7280; --primary: #0f766e; --border: #e5e7eb; --danger: #dc2626; }
        * { box-sizing: border-box; }
        body { margin: 0; font-family: "Helvetica Neue", "PingFang SC", sans-serif; background: linear-gradient(135deg, #f0fdf4, #eff6ff); color: var(--text); }
        .container { max-width: 720px; margin: 40px auto; background: var(--card); border: 1px solid var(--border); border-radius: 12px; padding: 24px; box-shadow: 0 10px 30px rgba(15,23,42,0.06); }
        .header { margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid var(--border); }
        h1 { margin: 0; font-size: 24px; }
        .detail-group { margin-bottom: 16px; }
        .label { font-size: 14px; color: var(--muted); margin-bottom: 4px; }
        .value { font-size: 16px; color: var(--text); }
        .price { font-size: 20px; font-weight: bold; color: var(--primary); }
        .actions { margin-top: 24px; display: flex; gap: 10px; flex-wrap: wrap; }
        .btn { border: none; border-radius: 8px; padding: 10px 14px; text-decoration: none; font-size: 14px; cursor: pointer; }
        .btn-primary { background: var(--primary); color: #fff; }
        .btn-secondary { background: #e2e8f0; color: #334155; }
        .btn-danger { background: #fee2e2; color: var(--danger); }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>固定资产详情</h1>
    </div>

    <div class="detail-group"><div class="label">ID</div><div class="value" th:text="${asset.id}"></div></div>
    <div class="detail-group"><div class="label">资产编号</div><div class="value" th:text="${asset.assetNo}"></div></div>
    <div class="detail-group"><div class="label">名称</div><div class="value" th:text="${asset.name}"></div></div>
    <div class="detail-group"><div class="label">分类</div><div class="value" th:text="${asset.category}"></div></div>
    <div class="detail-group"><div class="label">规格型号</div><div class="value" th:text="${asset.spec}"></div></div>
    <div class="detail-group"><div class="label">状态</div><div class="value" th:text="${asset.status}"></div></div>
    <div class="detail-group"><div class="label">原值</div><div class="price" th:text="${#numbers.formatDecimal(asset.originalValue, 1, 2) + ' 元'}"></div></div>
    <div class="detail-group"><div class="label">购置日期</div><div class="value" th:text="${asset.purchaseDate}"></div></div>
    <div class="detail-group"><div class="label">存放地点</div><div class="value" th:text="${asset.location}"></div></div>
    <div class="detail-group"><div class="label">备注</div><div class="value" th:text="${asset.remark}"></div></div>

    <div class="actions">
        <a class="btn btn-primary" th:href="@{/assets/{id}/edit(id=${asset.id})}">编辑资产</a>
        <a class="btn btn-secondary" th:href="@{/assets}">返回列表</a>
        <form th:action="@{/assets/{id}/delete(id=${asset.id})}" method="post" style="display:inline;">
            <button type="submit" class="btn btn-danger">删除资产</button>
        </form>
    </div>
</div>
</body>
</html>
```

- [ ] 4.4 启动应用做手工冒烟（验证视图渲染无异常）：

```bash
cd my-spring-boot-app && ./mvnw -q spring-boot:run &
sleep 25
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/assets
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/assets/new
# 停止应用
pkill -f spring-boot:run
```

预期：两个端点均返回 `200`。若 `404` 或 `500`，检查模板路径与 Controller view name 是否一致。

- [ ] 4.5 提交：

```bash
cd my-spring-boot-app && git add -A && git commit -m "feat(asset): Thymeleaf 视图 list/form/view + 内联校验错误"
```

---

## Task 5: 全量回归 + Self-Review

**Files:**
- 无新增；验证全仓测试与现有功能不受影响

**Context:** DIMA §8（测试策略）、§9（影响面：仅新增文件，不改动 Item/User）。确保 `FixedAsset` 模块与既有 `Item`/`User` 并存无冲突。

### Steps

- [ ] 5.1 运行全量测试（含既有 `MyAppApplicationTests`）：

```bash
cd my-spring-boot-app && ./mvnw -q test 2>&1 | tail -30
```

预期：`Tests run: 22`（`MyAppApplicationTests` 1 + `FixedAssetServiceTest` 12 + `FixedAssetControllerTest` 9），`Failures: 0, Errors: 0`。若总数不符，按实际用例数核对，但不得有失败。

- [ ] 5.2 验证既有 Item/User 功能未受影响（冒烟）：

```bash
cd my-spring-boot-app && ./mvnw -q spring-boot:run &
sleep 25
echo "items: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/items)"
echo "assets: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/assets)"
pkill -f spring-boot:run
```

预期：两者均 `200`。

- [ ] 5.3 Self-Review（实现者对照本节自检，非子代理派发）：

**1. Spec coverage（DIMA 章节核对）:**
- §3 架构组件 → Task 1（实体/仓储）、Task 2（Service）、Task 3（Controller）、Task 4（视图），全部覆盖
- §4 数据模型 12 字段 → Task 1.1 `FixedAsset.java` 12 字段逐字录入，✓
- §5.1 编号唯一性双保险 → Task 2.3 `save`/`update` 含 `existsByAssetNo`/`findByAssetNoForUpdate` + `DataIntegrityViolationException`，✓
- §5.2 状态字典四项 → Task 3.3 `STATUSES = Arrays.asList("在用","闲置","维修","报废")`，✓
- §5.3 分类聚合 → Task 1.2 `findAllCategories()` + Task 2.3 `getAllCategories()`，✓
- §5.4 检索 + 空关键字回退 → Task 1.2 `searchByKeyword` + Task 2.3 `searchByKeyword` 空白回退 `findAll()`，✓
- §6 端点表 10 个 → Task 3.3 全部实现，✓
- §7 错误处理（`IllegalArgumentException` + 不存在异常 + 删除预检）→ Task 2.3 + Task 3.3，✓
- §8 测试策略 → Task 1/2 Service 测试 + Task 3 Controller 测试，✓
- §9 影响面（仅新增，不改 Item/User/GlobalExceptionHandler/application.properties/pom.xml）→ 全程仅 Create 新文件，✓

**2. Placeholder scan:** 无 `TODO`/`TBD`/`implement later`/`add validation`/`handle edge cases` 等占位；每步均含完整代码与确切命令。✓

**3. Type consistency:**
- `FixedAsset` 字段名（`assetNo`/`name`/`category`/`spec`/`status`/`originalValue`/`userId`/`purchaseDate`/`location`/`remark`/`createdAt`/`updatedAt`）在 Task 1 实体、Task 2 Service、Task 3 Controller、Task 4 模板、测试中完全一致。✓
- `FixedAssetService` 方法名（`findAll`/`findById`/`save`/`update`/`deleteById`/`searchByKeyword`/`findByCategory`/`findByStatus`/`getAllCategories`）在 Task 2 定义、Task 3 调用、测试断言中一致。✓
- `FixedAssetRepository` 方法名（`existsByAssetNo`/`findByAssetNoForUpdate`/`searchByKeyword`/`findByCategory`/`findByStatus`/`findAllCategories`）在 Task 1 定义、Task 2 调用中一致。✓

- [ ] 5.4 提交（若 5.1/5.2 有修正）：

```bash
cd my-spring-boot-app && git add -A && git commit -m "test(asset): 全量回归通过 + Self-Review 自检" --allow-empty
```

---

## 执行交接（Execution Handoff）

本计划已保存至 `docs/plans/fixed-asset-management-plan.md`。两种执行选项：

1. **子代理驱动（推荐）** —— 按 Task 1→5 逐个派发独立子代理实现，每个 Task 结束审阅后再进入下一个，快速迭代。
2. **会话内执行** —— 在当前会话按 executing-plans 批量执行，逐步勾选 checkbox。

无论哪种方式，每个 Task 结束须运行该 Task 的测试命令并确认绿灯后再提交、进入下一 Task。
