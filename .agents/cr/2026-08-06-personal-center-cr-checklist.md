# Code Review Checklist

> **Change** `personal-center-management` · **分支/Commit** `AI/task-DEV-f4ad1a6e` / `d510525` · **日期** `2026-08-06`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。

---

## Step 1 — 文件列表与执行队列（产物 A）

| # | 文件 | 归属原因 | 状态 |
|---|------|----------|------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ItemController.java` | searchItems/getItemsByCategory 从分页改为全量 List | ✅ |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/exception/GlobalExceptionHandler.java` | 新增 isInternalUrl 修复开放重定向 | ✅ |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/models/Item.java` | 新增 userId 字段及 getter/setter | ✅ |
| 4 | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/ItemRepository.java` | 新增 findByNameForUpdate/findByUserId/searchByKeywordAndUserId | ✅ |
| 5 | `my-spring-boot-app/src/main/java/com/example/myapp/services/ItemService.java` | 新增 searchByKeywordAndUserId/findByUserId；清理重复 import | ✅ |

> **Java 守卫**：✅ 含 5 个 `.java` 文件，继续审查。

---

## Step 2 — 功能性检查（产物 B）

> Spec 来源：commit message `feat: add personal center (profile) management feature` + `个人中心信息编辑功能`。
> ⚠️ `<requirement_section>` 为占位文本 "test1111111111"，无正式 spec 文档，REQ 从代码变更推断。

| REQ | 功能点 | 结果 | 代码证据 |
|-----|--------|------|----------|
| REQ-1 | Item 模型新增 userId 字段 | ✅ | `Item.java:47-49` |
| REQ-2 | 支持按 userId 查询物品 | ✅ | `ItemRepository.java:33` / `ItemService.java:93-95` |
| REQ-3 | 支持按 userId + 关键词搜索 | ✅ | `ItemRepository.java:42-46` / `ItemService.java:85-90` |
| REQ-4 | 修复开放重定向漏洞 | ⚠️ | `GlobalExceptionHandler.java:34-48` — HTTPS 端口未处理 |
| REQ-5 | 悲观锁查重名（更新时） | ✅ | `ItemRepository.java:21-23` / `ItemService.java:53` |
| REQ-6 | searchItems/getItemsByCategory 改为全量返回 | ⚠️ | `ItemController.java:100,111` — 分页参数遗留未使用 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 结果 | 说明 |
|----|--------|------|------|
| A2.2 | 通配符 import | ❌ | `Item.java:3-4` `javax.persistence.*`/`javax.validation.constraints.*`；`ItemController.java:13` `web.bind.annotation.*`（预存） |
| A2.4 | 未使用 import | ❌ | `ItemController.java:6-9` Page/PageImpl/PageRequest/Pageable 新增后方法体已不使用（本次变更引入） |

---

## Step 4 — 可靠性检查（产物 D）

### scan-all-rules.sh 预扫结果（13 findings: P0=7, P1=3, P2=3）

| ID | 等级 | 文件:行 | 变更引入? | 说明 |
|----|------|---------|-----------|------|
| G16.2 | P0 | `GlobalExceptionHandler.java:44` | ✅ 是 | catch(Exception) 吞异常无日志（isInternalUrl 本次新增） |
| G16.2 | P0 | `ItemController.java:53` | ❌ 预存 | createItem catch 无日志 |
| G16.2 | P0 | `ItemController.java:78` | ❌ 预存 | updateItem catch 无日志 |
| G16.2 | P0 | `ItemController.java:89` | ❌ 预存 | deleteItem catch 无日志 |
| G16.2 | P0 | `ItemController.java:125` | ❌ 预存 | viewItem catch 无日志 |
| G16.2 | P0 | `ItemService.java:42` | ❌ 预存 | save catch 无日志 |
| G16.2 | P0 | `ItemService.java:66` | ❌ 预存 | update catch 无日志 |
| M016 | P1 | `Item.java:52,53,58` | ❌ 预存 | LocalDateTime.now() 默认时区 |
| A2.2 | P2 | `ItemController.java:13` | ❌ 预存 | 通配符 import |
| A2.2 | P2 | `Item.java:3` | ❌ 预存 | 通配符 import |
| A2.2 | P2 | `Item.java:4` | ❌ 预存 | 通配符 import |

### LLM 补充发现

| 等级 | 文件:行 | 说明 |
|------|---------|------|
| P1 | `ItemController.java:97-98,108-109` | 分页参数 page/size 保留在签名但方法体未使用，接口语义误导 |
| P1 | `GlobalExceptionHandler.java:40` | refererPort 默认 80 忽略 HTTPS 443，HTTPS 内部请求被误判为外部 |

---

## Step 5 — 自定义扩展检查（产物 E）

| 域 | 结果 | 说明 |
|----|------|------|
| 自定义扩展 | N/A | `customized-checklist.md` 未启用项目自定义规则 |

---

## 核销验证

- 执行队列 `⬜ 待审`：0（5/5 已审）✅
- report 审查范围文件数：5（与队列一致）✅
