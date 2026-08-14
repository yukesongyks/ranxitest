# util 模块

## 模块职责

提供通用工具类，不依赖 Spring 容器，纯 JDK 标准库实现。

## 关键类

| 类名 | 说明 |
|------|------|
| `QuickSort` | 快速排序工具类，支持泛型 Comparable 数组及 int 基本类型数组的原地排序 |

## 依赖关系

- 无外部模块依赖
- 无 Spring 依赖
- 纯 JDK 标准库（`java.lang.Comparable`）

## API 接口列表

| 方法签名 | 说明 |
|---------|------|
| `static <T extends Comparable<T>> void sort(T[] array)` | 对泛型 Comparable 数组进行原地快速排序 |
| `static void sort(int[] array)` | 对 int 基本类型数组进行原地快速排序 |