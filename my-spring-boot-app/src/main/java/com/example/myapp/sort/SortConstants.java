package com.example.myapp.sort;

/**
 * 排序模块常量定义。
 * <p>
 * 集中管理错误码、成功码及输入规模上限等魔法值，避免散落在代码各处的字面量。
 *
 * @author AiWork
 * @date 2026-09-15
 */
public final class SortConstants {

    private SortConstants() {
        // 工具类禁止实例化
    }

    /** 成功码。 */
    public static final String CODE_SUCCESS = "OK";

    /** 成功提示信息。 */
    public static final String MSG_SUCCESS = "SUCCESS";

    /** 错误码：待排序数组为空或 null。 */
    public static final String CODE_SORT_EMPTY = "A0001";

    /** 错误码：待排序元素数量超过上限。 */
    public static final String CODE_SORT_OVERSIZE = "A0002";

    /** 错误码：排序方向非法（仅允许 ASC/DESC）。 */
    public static final String CODE_SORT_INVALID_ORDER = "A0003";

    /** 错误码：待排序数组包含 null 元素。 */
    public static final String CODE_SORT_NULL_ELEMENT = "A0004";

    /** 单次排序元素数量上限。 */
    public static final int MAX_INPUT_SIZE = 1000;
}
