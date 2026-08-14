# Step 4: 接口设计

> 快速排序为纯工具类，无 REST API 接口。以下接口为类方法签名级别的内部接口。

## 4.1 oneapi（Web 控制台接口）
本项不适用，原因：快速排序为工具类，不暴露 HTTP 接口。

## 4.2 OpenAPI（对外接口）
本项不适用，原因：快速排序为工具类，不暴露 HTTP 接口。

## 4.3 内部接口（Service 层 / 工具类方法）

| 编号 | 接口名称 | 类 | 方法签名 |
|------|----------|------|----------|
| S01 | 整数数组升序排序 | QuickSort | `public static void sort(int[] arr)` |
| S02 | 整数数组排序（含方向） | QuickSort | `public static void sort(int[] arr, boolean ascending)` |
| S03 | 泛型数组排序 | QuickSort | `public static <T extends Comparable<T>> void sort(T[] arr)` |
| S04 | 泛型数组排序（含方向） | QuickSort | `public static <T extends Comparable<T>> void sort(T[] arr, boolean ascending)` |
| S05 | 自定义比较器排序 | QuickSort | `public static <T> void sort(T[] arr, Comparator<T> comparator)` |

## 4.4 集成接口（Integration 层）
本项不适用，原因：无外部系统集成。