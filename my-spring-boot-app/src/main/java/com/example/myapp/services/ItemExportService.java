package com.example.myapp.services;

import com.example.myapp.docgen.DocgenErrorCode;
import com.example.myapp.docgen.DocgenExportException;
import com.example.myapp.docgen.TxtRow;
import com.example.myapp.models.Item;
import com.example.myapp.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 物品 TXT 导出适配服务（item 模块）。
 *
 * <p>读取物品数据并组装为 docgen 通用行数据，字段顺序与表头约定一致：
 * ID/名称/描述/价格；转义由 docgen 模块统一处理。</p>
 */
@Service
public class ItemExportService {

    private static final Logger log = LoggerFactory.getLogger(ItemExportService.class);

    /** 导出文件名前缀（白名单固定前缀）。 */
    public static final String FILE_NAME_PREFIX = "items";

    /** 表头字段。 */
    public static final List<String> HEADERS = Arrays.asList("ID", "名称", "描述", "价格");

    private final ItemRepository itemRepository;

    /**
     * 构造适配服务。
     *
     * @param itemRepository 物品数据访问
     */
    public ItemExportService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * 组装物品清单 TXT 行数据。
     *
     * @return 行数据列表（不含表头）
     */
    public List<TxtRow> buildRows() {
        List<Item> items;
        try {
            items = itemRepository.findAll();
        } catch (RuntimeException e) {
            log.error("物品导出数据查询失败", e);
            throw new DocgenExportException(DocgenErrorCode.DATA_ASSEMBLY_FAILED,
                    DocgenErrorCode.DATA_ASSEMBLY_FAILED.getDefaultMessage());
        }
        List<TxtRow> rows = new ArrayList<>(items.size());
        for (Item item : items) {
            rows.add(new TxtRow(Arrays.asList(
                    String.valueOf(item.getId()),
                    item.getName(),
                    item.getDescription() == null ? "" : item.getDescription(),
                    formatPrice(item.getPrice()))));
        }
        return rows;
    }

    private String formatPrice(BigDecimal price) {
        return price == null ? "" : price.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}