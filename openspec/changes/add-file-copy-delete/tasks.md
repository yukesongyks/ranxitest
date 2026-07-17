# Tasks: 快捷 Copy 与快捷 Delete 文件

## 实现区域：Service 层

- [ ] **Task 1**: 在 `ItemService.java` 中新增 `copy(Long id)` 方法
  - 通过 `findById(id)` 获取原物品
  - 若不存在则抛出 `IllegalArgumentException("物品不存在，无法复制")`
  - 创建新 Item 实例，复制 name、description、category、price、quantity 字段
  - 不设置 id（留给 JPA 自动生成），不复制 createdAt/updatedAt
  - 返回新 Item 实例（不持久化，由 Controller 放入 Model 供编辑页使用）

## 实现区域：Controller 层

- [ ] **Task 2**: 在 `ItemController.java` 中新增 `GET /items/{id}/copy` 端点
  - 调用 `itemService.copy(id)` 获取副本
  - 将副本放入 Model
  - 添加 Flash 属性"物品复制成功，请修改后保存"
  - 返回 `items/form` 编辑页视图

- [ ] **Task 3**: 在 `ItemController.java` 中新增 `POST /items/{id}/quick-delete` 端点
  - 调用 `itemService.deleteById(id)`
  - 成功时添加 Flash 属性"物品删除成功！"
  - 失败时（`IllegalArgumentException`）添加 Flash 错误属性
  - 重定向到 `/items`

## 实现区域：View 层

- [ ] **Task 4**: 修改 `templates/items/list.html`，在每行物品操作区添加"复制"按钮
  - 链接到 `@{/items/{id}/copy}`（GET 请求）
  - 按钮样式与现有操作按钮一致

- [ ] **Task 5**: 修改 `templates/items/list.html`，在每行物品操作区添加"快捷删除"按钮
  - 使用 POST 表单提交到 `@{/items/{id}/quick-delete}`
  - 按钮样式与现有操作按钮一致

- [ ] **Task 6**: 修改 `templates/items/view.html`（详情页），添加"复制"按钮
  - 链接到 `@{/items/{id}/copy}`（GET 请求）

## 验证

- [ ] **Task 7**: 手动验证：启动应用，在列表页点击"复制"，确认跳转编辑页且字段已填充原物品数据
- [ ] **Task 8**: 手动验证：在列表页点击"快捷删除"，确认物品被删除且列表刷新
- [ ] **Task 9**: 手动验证：尝试复制不存在的物品 ID，确认错误提示并重定向回列表
- [ ] **Task 10**: 手动验证：尝试删除不存在的物品 ID，确认错误提示并重定向回列表