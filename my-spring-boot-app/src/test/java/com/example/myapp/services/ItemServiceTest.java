package com.example.myapp.services;

import com.example.myapp.models.Item;
import com.example.myapp.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

/**
 * {@link ItemService} 单元测试。
 * <p>
 * 技术栈：JUnit 5 + Mockito + AssertJ（遵循项目 spring-boot-starter-test 约定）。
 * Mock 策略：仅 Mock {@link ItemRepository}（外部依赖），值对象 {@link Item} 直接 new。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService 单元测试")
class ItemServiceTest {

    private static final Long TEST_ITEM_ID = 1L;
    private static final String TEST_ITEM_NAME = "笔记本电脑";

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    // ==================== findAll / findById / findByName 测试 ====================

    @Test
    @DisplayName("查询全部物品：应返回物品列表")
    void should_returnAllItems() {
        // Arrange
        Item item1 = buildItem(1L, "物品A");
        Item item2 = buildItem(2L, "物品B");
        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        // Act
        List<Item> result = itemService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Item::getName).containsExactly("物品A", "物品B");
    }

    @Test
    @DisplayName("根据ID查询物品：物品存在时，应返回包含该物品的 Optional")
    void should_returnItem_when_idExists() {
        // Arrange
        Item existingItem = buildItem(TEST_ITEM_ID, TEST_ITEM_NAME);
        when(itemRepository.findById(TEST_ITEM_ID)).thenReturn(Optional.of(existingItem));

        // Act
        Optional<Item> result = itemService.findById(TEST_ITEM_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(TEST_ITEM_NAME);
    }

    @Test
    @DisplayName("根据ID查询物品：物品不存在时，应返回空 Optional")
    void should_returnEmpty_when_idDoesNotExist() {
        // Arrange
        when(itemRepository.findById(TEST_ITEM_ID)).thenReturn(Optional.empty());

        // Act
        Optional<Item> result = itemService.findById(TEST_ITEM_ID);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("根据名称查询物品：物品存在时，应返回包含该物品的 Optional")
    void should_returnItem_when_nameExists() {
        // Arrange
        Item existingItem = buildItem(TEST_ITEM_ID, TEST_ITEM_NAME);
        when(itemRepository.findByName(TEST_ITEM_NAME)).thenReturn(Optional.of(existingItem));

        // Act
        Optional<Item> result = itemService.findByName(TEST_ITEM_NAME);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(TEST_ITEM_NAME);
    }

    // ==================== save 测试 ====================

    @Test
    @DisplayName("保存物品：ID为null且名称不重复时，应成功保存")
    void should_saveNewItem_when_idIsNullAndNameIsUnique() {
        // Arrange
        Item newItem = buildItem(null, TEST_ITEM_NAME);
        when(itemRepository.existsByName(TEST_ITEM_NAME)).thenReturn(false);
        when(itemRepository.save(newItem)).thenReturn(newItem);

        // Act
        Item result = itemService.save(newItem);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(TEST_ITEM_NAME);
        verify(itemRepository, times(1)).save(newItem);
    }

    @Test
    @DisplayName("保存物品：ID为null但名称已存在时，应抛出 IllegalArgumentException")
    void should_throwException_when_newItemNameAlreadyExists() {
        // Arrange
        Item newItem = buildItem(null, TEST_ITEM_NAME);
        when(itemRepository.existsByName(TEST_ITEM_NAME)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> itemService.save(newItem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(TEST_ITEM_NAME)
                .hasMessageContaining("已存在");
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("保存物品：ID不为null（更新场景）时，应跳过重名检查直接保存")
    void should_saveItem_when_idIsNotNull() {
        // Arrange
        Item existingItem = buildItem(TEST_ITEM_ID, TEST_ITEM_NAME);
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        // Act
        Item result = itemService.save(existingItem);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ITEM_ID);
        verify(itemRepository, never()).existsByName(any());
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    @DisplayName("保存物品：底层抛出 DataIntegrityViolationException 时，应转换为 IllegalArgumentException")
    void should_throwIllegalArgumentException_when_dataIntegrityViolationOccurs() {
        // Arrange
        Item newItem = buildItem(null, TEST_ITEM_NAME);
        when(itemRepository.existsByName(TEST_ITEM_NAME)).thenReturn(false);
        when(itemRepository.save(newItem))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        // Act & Assert
        assertThatThrownBy(() -> itemService.save(newItem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(TEST_ITEM_NAME)
                .hasMessageContaining("已存在")
                .hasCauseInstanceOf(DataIntegrityViolationException.class);
    }

    // ==================== update 测试 ====================

    @Test
    @DisplayName("更新物品：物品存在且名称未变更时，应成功更新")
    void should_updateItem_when_nameUnchanged() {
        // Arrange
        Item existingItem = buildItem(TEST_ITEM_ID, TEST_ITEM_NAME);
        existingItem.setQuantity(10);

        Item itemDetails = buildItem(null, TEST_ITEM_NAME);
        itemDetails.setDescription("更新描述");
        itemDetails.setQuantity(5);

        when(itemRepository.findById(TEST_ITEM_ID)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);

        // Act
        Item result = itemService.update(TEST_ITEM_ID, itemDetails);

        // Assert
        assertThat(result.getDescription()).isEqualTo("更新描述");
        assertThat(result.getQuantity()).isEqualTo(5);
        verify(itemRepository, never()).findByNameForUpdate(any());
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    @DisplayName("更新物品：物品不存在时，应抛出 IllegalArgumentException")
    void should_throwException_when_itemNotFoundDuringUpdate() {
        // Arrange
        Item itemDetails = buildItem(null, "新名称");
        when(itemRepository.findById(TEST_ITEM_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.update(TEST_ITEM_ID, itemDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("物品不存在")
                .hasMessageContaining(String.valueOf(TEST_ITEM_ID));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("更新物品：新名称与其他物品冲突时，应抛出 IllegalArgumentException")
    void should_throwException_when_newNameConflictsWithExistingItem() {
        // Arrange
        Item existingItem = buildItem(TEST_ITEM_ID, TEST_ITEM_NAME);
        Item itemDetails = buildItem(null, "冲突名称");

        when(itemRepository.findById(TEST_ITEM_ID)).thenReturn(Optional.of(existingItem));
        when(itemRepository.findByNameForUpdate("冲突名称"))
                .thenReturn(Optional.of(buildItem(99L, "冲突名称")));

        // Act & Assert
        assertThatThrownBy(() -> itemService.update(TEST_ITEM_ID, itemDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("冲突名称")
                .hasMessageContaining("已存在");
        verify(itemRepository, never()).save(any(Item.class));
    }

    // ==================== deleteById 测试 ====================

    @Test
    @DisplayName("删除物品：物品存在时，应成功删除")
    void should_deleteItem_when_itemExists() {
        // Arrange
        when(itemRepository.existsById(TEST_ITEM_ID)).thenReturn(true);

        // Act
        itemService.deleteById(TEST_ITEM_ID);

        // Assert
        verify(itemRepository, times(1)).deleteById(TEST_ITEM_ID);
    }

    @Test
    @DisplayName("删除物品：物品不存在时，应抛出 IllegalArgumentException")
    void should_throwException_when_deleteNonExistentItem() {
        // Arrange
        when(itemRepository.existsById(TEST_ITEM_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> itemService.deleteById(TEST_ITEM_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("物品不存在")
                .hasMessageContaining(String.valueOf(TEST_ITEM_ID));
        verify(itemRepository, never()).deleteById(any());
    }

    // ==================== searchByKeyword 测试 ====================

    @Test
    @DisplayName("关键词搜索：关键词为null时，应返回全部物品")
    void should_returnAllItems_when_keywordIsNull() {
        // Arrange
        Item item1 = buildItem(1L, "物品A");
        when(itemRepository.findAll()).thenReturn(List.of(item1));

        // Act
        List<Item> result = itemService.searchByKeyword(null);

        // Assert
        assertThat(result).hasSize(1);
        verify(itemRepository, never()).searchByKeyword(any());
    }

    @Test
    @DisplayName("关键词搜索：关键词为空白字符串时，应返回全部物品")
    void should_returnAllItems_when_keywordIsBlank() {
        // Arrange
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Item> result = itemService.searchByKeyword("   ");

        // Assert
        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchByKeyword(any());
    }

    @Test
    @DisplayName("关键词搜索：关键词非空时，应委托 Repository 执行搜索并去除首尾空白")
    void should_searchByKeyword_when_keywordIsNotBlank() {
        // Arrange
        Item matchedItem = buildItem(1L, "笔记本电脑");
        String keyword = "  电脑  ";
        when(itemRepository.searchByKeyword("电脑")).thenReturn(List.of(matchedItem));

        // Act
        List<Item> result = itemService.searchByKeyword(keyword);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("笔记本电脑");
        verify(itemRepository, times(1)).searchByKeyword("电脑");
    }

    // ==================== searchByKeywordAndUserId 测试 ====================

    @Test
    @DisplayName("按用户关键词搜索：关键词为null时，应返回该用户全部物品")
    void should_returnItemsByUser_when_keywordIsNull() {
        // Arrange
        Long userId = 5L;
        Item item = buildItem(1L, TEST_ITEM_NAME);
        when(itemRepository.findByUserId(userId)).thenReturn(List.of(item));

        // Act
        List<Item> result = itemService.searchByKeywordAndUserId(null, userId);

        // Assert
        assertThat(result).hasSize(1);
        verify(itemRepository, never()).searchByKeywordAndUserId(any(), any());
    }

    @Test
    @DisplayName("按用户关键词搜索：关键词为空白时，应返回该用户全部物品")
    void should_returnItemsByUser_when_keywordIsBlank() {
        // Arrange
        Long userId = 5L;
        when(itemRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<Item> result = itemService.searchByKeywordAndUserId("  ", userId);

        // Assert
        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchByKeywordAndUserId(any(), any());
    }

    @Test
    @DisplayName("按用户关键词搜索：关键词非空时，应委托 Repository 执行搜索")
    void should_searchByKeywordAndUserId_when_keywordIsNotBlank() {
        // Arrange
        Long userId = 5L;
        Item matchedItem = buildItem(1L, TEST_ITEM_NAME);
        String keyword = "  电脑  ";
        when(itemRepository.searchByKeywordAndUserId("电脑", userId)).thenReturn(List.of(matchedItem));

        // Act
        List<Item> result = itemService.searchByKeywordAndUserId(keyword, userId);

        // Assert
        assertThat(result).hasSize(1);
        verify(itemRepository, times(1)).searchByKeywordAndUserId("电脑", userId);
    }

    // ==================== findByCategory / findByUserId / findLowStockItems 测试 ====================

    @Test
    @DisplayName("按分类查询：应委托 Repository 执行分类查询并返回匹配物品")
    void should_returnItemsByCategory() {
        // Arrange
        String category = "电子产品";
        Item item = buildItem(1L, TEST_ITEM_NAME);
        when(itemRepository.findByCategory(category)).thenReturn(List.of(item));

        // Act
        List<Item> result = itemService.findByCategory(category);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).as("分类应与构造数据一致").isEqualTo("电子产品");
        verify(itemRepository, times(1)).findByCategory(category);
    }

    @Test
    @DisplayName("按用户ID查询：应委托 Repository 执行用户物品查询")
    void should_returnItemsByUserId() {
        // Arrange
        Long userId = 10L;
        Item item = buildItem(1L, TEST_ITEM_NAME);
        when(itemRepository.findByUserId(userId)).thenReturn(List.of(item));

        // Act
        List<Item> result = itemService.findByUserId(userId);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("低库存查询：应委托 Repository 执行库存阈值查询")
    void should_returnLowStockItems() {
        // Arrange
        Integer threshold = 10;
        Item lowStockItem = buildItem(1L, "低库存物品");
        when(itemRepository.findByQuantityLessThan(threshold)).thenReturn(List.of(lowStockItem));

        // Act
        List<Item> result = itemService.findLowStockItems(threshold);

        // Assert
        assertThat(result).hasSize(1);
        verify(itemRepository, times(1)).findByQuantityLessThan(threshold);
    }

    // ==================== getAllCategories 测试 ====================

    @Test
    @DisplayName("查询所有分类：应返回去重后的分类列表")
    void should_returnAllCategories() {
        // Arrange
        when(itemRepository.findAllCategories()).thenReturn(List.of("电子产品", "食品", "日用品"));

        // Act
        List<String> result = itemService.getAllCategories();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("电子产品", "食品", "日用品");
        verify(itemRepository, times(1)).findAllCategories();
    }

    @Test
    @DisplayName("查询所有分类：无数据时应返回空列表")
    void should_returnEmptyList_when_noCategoriesExist() {
        // Arrange
        when(itemRepository.findAllCategories()).thenReturn(Collections.emptyList());

        // Act
        List<String> result = itemService.getAllCategories();

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== 测试数据构造方法 ====================

    /**
     * 构造测试用 Item 对象。
     *
     * @param id   物品ID，新建场景传 null
     * @param name 物品名称
     * @return 构造完成的 Item 实例
     */
    private Item buildItem(Long id, String name) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setCategory("电子产品");
        item.setQuantity(10);
        item.setPrice(new BigDecimal("9999.00"));
        return item;
    }
}
