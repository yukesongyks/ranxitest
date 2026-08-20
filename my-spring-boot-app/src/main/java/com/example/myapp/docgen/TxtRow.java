package com.example.myapp.docgen;

import java.util.Collections;
import java.util.List;

/**
 * 通用 TXT 行数据模型。
 *
 * <p>一行由一个有序单元格列表组成，docgen 模块据其生成 TXT 文本行。</p>
 */
public class TxtRow {

    /** 单元格列表（按列顺序）。 */
    private final List<String> cells;

    /**
     * 构造 TXT 行。
     *
     * @param cells 单元格列表
     */
    public TxtRow(List<String> cells) {
        this.cells = Collections.unmodifiableList(cells);
    }

    public List<String> getCells() {
        return cells;
    }
}