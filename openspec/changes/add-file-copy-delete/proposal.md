# Proposal: 快捷 Copy 与快捷 Delete 文件功能

## Intent
为现有的物品/文件管理系统（Item CRUD）新增两个快捷操作：**一键复制（Copy）** 和 **快捷删除（Quick Delete）**，提升用户操作效率。

## Scope
- **快捷 Copy**：在物品列表页每个物品行旁增加"复制"按钮，点击后以当前物品数据为模板，创建一份副本（新 ID、新创建时间），跳转至编辑页供用户修改后保存。
- **快捷 Delete**：在物品列表页每个物品行旁增加"删除"按钮（已有详情页删除），点击后直接删除当前物品，无需进入详情页，删除后刷新列表并显示成功提示。

## Non-Goals
- 不涉及批量操作（批量复制/删除）。
- 不涉及撤销/回收站机制。
- 不涉及复制时关联数据的级联处理（当前 Item 无关联子实体）。
- 不新增 REST API 端点（沿用现有 MVC Controller 模式）。

## Affected Areas
| 层级 | 文件 | 变更类型 |
|------|------|----------|
| Controller | `ItemController.java` | 新增 `copyItem`、`quickDeleteItem` 端点 |
| Service | `ItemService.java` | 新增 `copy(Long id)` 方法 |
| View | `templates/items/list.html` | 列表页新增"复制"和"删除"按钮 |
| I18n | `messages*.properties`（如有） | 新增提示文案 |

## Risk
- **低风险**：改动仅涉及现有 Item 模块的 Controller/Service/View 层，不修改数据模型，不引入新依赖。
- 复制操作需确保新生成的 Item 保留所有可复制字段（名称、描述、分类、价格、数量），但重置 ID、创建时间、更新时间。

## Rollback
- 回滚相关 Controller 方法和视图模板即可恢复原状，无需数据迁移。

## Assumptions
- 用户确认"快捷 Copy"含义为基于现有物品创建副本，而非文件系统级别的文件复制。
- 删除操作沿用现有 `deleteById` 逻辑，无需二次确认弹窗（快捷设计意图）。