# Proposal: 实现快速排序算法

## 意图
在 `com.example.myapp.utils` 包下实现一个通用的快速排序（QuickSort）工具类。

## 范围
- 新增 `QuickSort.java` 工具类，提供泛型快速排序方法
- 支持 `Comparable<T>` 自然排序和 `Comparator<T>` 自定义排序
- 使用原地（in-place）分区实现，避免额外内存分配
- 新增 `QuickSortTest.java` 单元测试覆盖基本场景

## 技术决策
- 语言：Java 17
- 实现方式：递归 + Lomuto 分区方案
- 三数取中（median-of-three）优化，避免最坏情况退化