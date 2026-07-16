# Proposal: Implement QuickSort Algorithm

## Intent
在 `com.example.myapp.utils` 包中实现一个通用的快速排序算法（QuickSort），支持泛型 `Comparable` 类型排序，并附带完整的单元测试。

## Scope
- 新增 `QuickSort.java` 工具类，提供静态排序方法
- 实现原地（in-place）快速排序，选取中间元素作为 pivot
- 支持泛型 `T extends Comparable<T>` 的数组排序
- 新增 `QuickSortTest.java` 单元测试覆盖边界条件

## Non-Goals
- 不实现多线程并行排序
- 不实现自定义 Comparator 版本（仅支持 Comparable）
- 不修改现有业务代码