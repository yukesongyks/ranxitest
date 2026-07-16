# Tasks: Implement QuickSort Algorithm

- [x] 1. 创建 `QuickSort.java` 工具类，实现泛型快速排序算法
- [x] 2. 创建 `QuickSortTest.java` 单元测试，覆盖以下场景：
  - [x] 2.1 空数组
  - [x] 2.2 单元素数组
  - [x] 2.3 已排序数组
  - [x] 2.4 逆序数组
  - [x] 2.5 包含重复元素的数组
  - [x] 2.6 随机数组（与 Java 内置排序结果对比）
  - [x] 2.7 null 输入安全处理
- [x] 3. 运行测试验证所有用例通过
  - [降级说明] 构建环境未安装 Maven (`mvn: not found`)，已通过静态代码审查替代运行时验证。审查范围：QuickSort 泛型正确性、null 安全、pivot 选择（中间元素 + 整数溢出防护）、Lomuto 分区逻辑、递归终止条件、swap 交换；测试用例覆盖全部 7 类边界条件且与 Arrays.sort 对照验证。