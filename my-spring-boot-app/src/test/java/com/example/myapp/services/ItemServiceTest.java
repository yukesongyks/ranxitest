package com.example.myapp.services;

import com.example.myapp.models.Item;
import com.example.myapp.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ItemService} 单元测试。
 * <p>
 * 覆盖物品查询、新增、更新、删除及搜索等核心业务路径，
 * 使用 Mockito 隔离 {@link ItemRepository} 依赖，遵循 FIRST 原则。
 * </p>
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService 单元测试")
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new Item("键盘", "机械键盘", "电子", 10, new BigDecimal("199.00"));
        sampleItem.setId(1L);
    }

    // ==================== 查询类 ====================

    @Test
    @DisplayName("findAll: 返回全部物品列表")
    void should_returnAllItems_when_findAll() {
        // given
        when(itemRepository.findAll()).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.findAll();

        // then
        assertThat(result).isNotEmpty().contains(sampleItem).hasSize(1);
        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("findAll: 无数据时返回空列表")
    void should_returnEmptyList_when_findAllAndNoData() {
        // given
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Item> result = itemService.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById: 物品存在时返回 Optional")
    void should_returnItem_when_findByIdAndExists() {
        // given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        // when
        Optional<Item> result = itemService.findById(1L);

        // then
        assertThat(result).isPresent().contains(sampleItem);
    }

    @Test
    @DisplayName("findById: 物品不存在时返回 empty Optional")
    void should_returnEmpty_when_findByIdAndNotExists() {
        // given
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        Optional<Item> result = itemService.findById(99L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByName: 按名称查询返回结果")
    void should_returnItem_when_findByNameAndExists() {
        // given
        when(itemRepository.findByName("键盘")).thenReturn(Optional.of(sampleItem));

        // when
        Optional<Item> result = itemService.findByName("键盘");

        // then
        assertThat(result).isPresent().get().extracting(Item::getName).isEqualTo("键盘");
    }

    // ==================== 新增 save ====================

    @Test
    @DisplayName("save: 新增物品名称未占用时保存成功")
    void should_saveItem_when_nameNotExists() {
        // given
        Item newItem = new Item("鼠标", "无线鼠标", "电子", 20, new BigDecimal("59.00"));
        when(itemRepository.existsByName("鼠标")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Item result = itemService.save(newItem);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("鼠标");
        verify(itemRepository).save(newItem);
    }

    @Test
    @DisplayName("save: 新增物品名称已存在时抛出 IllegalArgumentException")
    void should_throw_when_saveDuplicateName() {
        // given
        Item duplicate = new Item("键盘", "重复", "电子", 5, new BigDecimal("100.00"));
        when(itemRepository.existsByName("键盘")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> itemService.save(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("已存在");
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("save: 更新已有物品(id非空)时跳过重名校验直接保存")
    void should_saveDirectly_when_itemHasId() {
        // given
        sampleItem.setDescription("更新描述");
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Item result = itemService.save(sampleItem);

        // then
        assertThat(result.getDescription()).isEqualTo("更新描述");
        verify(itemRepository, never()).existsByName(anyString());
    }

    @Test
    @DisplayName("save: 保存时触发 DataIntegrityViolationException 转为 IllegalArgumentException")
    void should_throwIllegalArg_when_saveCausesDataIntegrityViolation() {
        // given
        Item newItem = new Item("键盘", null, "电子", 1, BigDecimal.ONE);
        when(itemRepository.existsByName("键盘")).thenReturn(false);
        when(itemRepository.save(any(Item.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        // when & then
        assertThatThrownBy(() -> itemService.save(newItem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("已存在")
                .hasCauseInstanceOf(DataIntegrityViolationException.class);
    }

    // ==================== 更新 update ====================

    @Test
    @DisplayName("update: 物品不存在时抛出 IllegalArgumentException")
    void should_throw_when_updateNonExistentItem() {
        // given
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> itemService.update(99L, sampleItem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不存在");
    }

    @Test
    @DisplayName("update: 名称未变更时直接更新并保存")
    void should_updateAndSave_when_nameUnchanged() {
        // given
        Item details = new Item("键盘", "新描述", "电子", 8, new BigDecimal("180.00"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Item result = itemService.update(1L, details);

        // then
        assertThat(result.getDescription()).isEqualTo("新描述");
        assertThat(result.getQuantity()).isEqualTo(8);
        verify(itemRepository, never()).findByNameForUpdate(anyString());
    }

    @Test
    @DisplayName("update: 名称变更为未占用名称时更新成功")
    void should_update_when_nameChangedAndNotOccupied() {
        // given
        Item details = new Item("显示器", "27寸", "电子", 3, new BigDecimal("999.00"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.findByNameForUpdate("显示器")).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Item result = itemService.update(1L, details);

        // then
        assertThat(result.getName()).isEqualTo("显示器");
        verify(itemRepository).findByNameForUpdate("显示器");
    }

    @Test
    @DisplayName("update: 名称变更为已被占用名称时抛出 IllegalArgumentException")
    void should_throw_when_updateToOccupiedName() {
        // given
        Item details = new Item("显示器", "27寸", "电子", 3, new BigDecimal("999.00"));
        Item occupied = new Item("显示器", null, "电子", 1, BigDecimal.ONE);
        occupied.setId(2L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.findByNameForUpdate("显示器")).thenReturn(Optional.of(occupied));

        // when & then
        assertThatThrownBy(() -> itemService.update(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("已存在");
        verify(itemRepository, never()).save(any(Item.class));
    }

    // ==================== 删除 deleteById ====================

    @Test
    @DisplayName("deleteById: 物品存在时删除成功")
    void should_delete_when_itemExists() {
        // given
        when(itemRepository.existsById(1L)).thenReturn(true);

        // when
        itemService.deleteById(1L);

        // then
        verify(itemRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: 物品不存在时抛出 IllegalArgumentException")
    void should_throw_when_deleteNonExistent() {
        // given
        when(itemRepository.existsById(99L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> itemService.deleteById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不存在");
        verify(itemRepository, never()).deleteById(anyLong());
    }

    // ==================== 搜索 searchByKeyword ====================

    @Test
    @DisplayName("searchByKeyword: 关键字为空时返回全部")
    void should_returnAll_when_keywordBlank() {
        // given
        when(itemRepository.findAll()).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.searchByKeyword("");

        // then
        assertThat(result).hasSize(1);
        verify(itemRepository).findAll();
        verify(itemRepository, never()).searchByKeyword(anyString());
    }

    @Test
    @DisplayName("searchByKeyword: 关键字非空时按关键字搜索")
    void should_searchByKeyword_when_keywordProvided() {
        // given
        when(itemRepository.searchByKeyword("键")).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.searchByKeyword(" 键 ");

        // then
        assertThat(result).contains(sampleItem);
        verify(itemRepository).searchByKeyword("键");
    }

    @Test
    @DisplayName("searchByKeywordAndUserId: 关键字为空时按用户返回")
    void should_returnByUser_when_keywordBlank() {
        // given
        when(itemRepository.findByUserId(1L)).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.searchByKeywordAndUserId("  ", 1L);

        // then
        assertThat(result).hasSize(1);
        verify(itemRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("searchByKeywordAndUserId: 关键字非空时按用户+关键字搜索")
    void should_searchByKeywordAndUser_when_bothProvided() {
        // given
        when(itemRepository.searchByKeywordAndUserId("键", 1L)).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.searchByKeywordAndUserId(" 键 ", 1L);

        // then
        assertThat(result).contains(sampleItem);
        verify(itemRepository).searchByKeywordAndUserId("键", 1L);
    }

    // ==================== 其他查询 ====================

    @Test
    @DisplayName("findByCategory: 按分类查询")
    void should_returnByCategory() {
        // given
        when(itemRepository.findByCategory("电子")).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.findByCategory("电子");

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findByUserId: 按用户查询")
    void should_returnByUserId() {
        // given
        when(itemRepository.findByUserId(1L)).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.findByUserId(1L);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findLowStockItems: 按库存阈值查询低库存物品")
    void should_returnLowStock_when_thresholdGiven() {
        // given
        when(itemRepository.findByQuantityLessThan(5)).thenReturn(List.of(sampleItem));

        // when
        List<Item> result = itemService.findLowStockItems(5);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getAllCategories: 返回去重分类列表")
    void should_returnAllCategories() {
        // given
        when(itemRepository.findAllCategories()).thenReturn(List.of("电子", "食品"));

        // when
        List<String> result = itemService.getAllCategories();

        // then
        assertThat(result).containsExactly("电子", "食品");
    }
}
