package com.example.myapp.services;

import com.example.myapp.models.Item;
import com.example.myapp.repositories.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService 单元测试")
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item createDefaultItem() {
        Item item = new Item("Test Item", "Test description", "Electronics", 10, new BigDecimal("99.99"));
        item.setId(1L);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return item;
    }

    // ==================== findAll 测试 ====================

    @Nested
    @DisplayName("findAll 方法")
    class FindAll {

        @Test
        @DisplayName("返回所有物品列表")
        void should_returnAllItems() {
            // Arrange
            Item item1 = createDefaultItem();
            Item item2 = new Item("Item2", "Desc2", "Books", 5, new BigDecimal("29.99"));
            item2.setId(2L);
            when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

            // Act
            List<Item> result = itemService.findAll();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Item::getName).contains("Test Item", "Item2");
        }

        @Test
        @DisplayName("无物品时返回空列表")
        void should_returnEmptyList_when_noItems() {
            // Arrange
            when(itemRepository.findAll()).thenReturn(List.of());

            // Act
            List<Item> result = itemService.findAll();

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // ==================== findById 测试 ====================

    @Nested
    @DisplayName("findById 方法")
    class FindById {

        @Test
        @DisplayName("物品存在时返回物品")
        void should_returnItem_when_itemExists() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

            // Act
            Optional<Item> result = itemService.findById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Test Item");
        }

        @Test
        @DisplayName("物品不存在时返回空")
        void should_returnEmpty_when_itemNotExists() {
            // Arrange
            when(itemRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<Item> result = itemService.findById(999L);

            // Assert
            assertThat(result).isNotPresent();
        }
    }

    // ==================== findByName 测试 ====================

    @Nested
    @DisplayName("findByName 方法")
    class FindByName {

        @Test
        @DisplayName("按名称查找存在时返回物品")
        void should_returnItem_when_nameExists() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findByName("Test Item")).thenReturn(Optional.of(item));

            // Act
            Optional<Item> result = itemService.findByName("Test Item");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Test Item");
        }

        @Test
        @DisplayName("按名称查找不存在时返回空")
        void should_returnEmpty_when_nameNotExists() {
            // Arrange
            when(itemRepository.findByName("NonExistent")).thenReturn(Optional.empty());

            // Act
            Optional<Item> result = itemService.findByName("NonExistent");

            // Assert
            assertThat(result).isNotPresent();
        }
    }

    // ==================== save 测试 ====================

    @Nested
    @DisplayName("save 方法")
    class Save {

        @Test
        @DisplayName("正常保存新物品 - 名称不重复")
        void should_saveItem_when_nameNotDuplicate() {
            // Arrange
            Item item = createDefaultItem();
            item.setId(null); // 新物品无ID
            when(itemRepository.existsByName(item.getName())).thenReturn(false);
            when(itemRepository.save(any(Item.class))).thenReturn(item);

            // Act
            Item result = itemService.save(item);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Test Item");
            verify(itemRepository, times(1)).save(item);
        }

        @Test
        @DisplayName("新物品名称已存在时抛出异常")
        void should_throwException_when_nameAlreadyExistsForNewItem() {
            // Arrange
            Item item = createDefaultItem();
            item.setId(null);
            when(itemRepository.existsByName(item.getName())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品名称 'Test Item' 已存在");
            verify(itemRepository, never()).save(any());
        }

        @Test
        @DisplayName("数据完整性冲突时抛出异常")
        void should_throwException_when_dataIntegrityViolation() {
            // Arrange
            Item item = createDefaultItem();
            item.setId(null);
            when(itemRepository.existsByName(item.getName())).thenReturn(false);
            when(itemRepository.save(any(Item.class))).thenThrow(new DataIntegrityViolationException("Duplicate key"));

            // Act & Assert
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品名称 'Test Item' 已存在");
        }
    }

    // ==================== update 测试 ====================

    @Nested
    @DisplayName("update 方法")
    class Update {

        @Test
        @DisplayName("正常更新物品 - 名称未变更")
        void should_updateItem_when_nameUnchanged() {
            // Arrange
            Item existingItem = createDefaultItem();
            Item itemDetails = createDefaultItem();
            itemDetails.setDescription("Updated description");
            itemDetails.setQuantity(20);

            when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
            when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

            // Act
            Item result = itemService.update(1L, itemDetails);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getDescription()).isEqualTo("Updated description");
            assertThat(result.getQuantity()).isEqualTo(20);
            verify(itemRepository, times(1)).save(existingItem);
        }

        @Test
        @DisplayName("更新时名称已存在且非本人则抛出异常")
        void should_throwException_when_nameAlreadyUsedByOther() {
            // Arrange
            Item existingItem = createDefaultItem();
            Item itemDetails = createDefaultItem();
            itemDetails.setName("OtherItem");

            Item otherItem = createDefaultItem();
            otherItem.setId(2L);
            otherItem.setName("OtherItem");

            when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
            when(itemRepository.findByNameForUpdate("OtherItem")).thenReturn(Optional.of(otherItem));

            // Act & Assert
            assertThatThrownBy(() -> itemService.update(1L, itemDetails))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品名称 'OtherItem' 已存在");
            verify(itemRepository, never()).save(any());
        }

        @Test
        @DisplayName("物品不存在时抛出异常")
        void should_throwException_when_itemNotFound() {
            // Arrange
            Item itemDetails = createDefaultItem();
            when(itemRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> itemService.update(999L, itemDetails))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品不存在，ID: 999");
        }

        @Test
        @DisplayName("更新时数据完整性冲突抛出异常")
        void should_throwException_when_dataIntegrityViolationOnUpdate() {
            // Arrange
            Item existingItem = createDefaultItem();
            Item itemDetails = createDefaultItem();
            itemDetails.setName("OtherItem");

            when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
            when(itemRepository.findByNameForUpdate("OtherItem")).thenReturn(Optional.empty());
            when(itemRepository.save(any(Item.class))).thenThrow(new DataIntegrityViolationException("Duplicate key"));

            // Act & Assert
            assertThatThrownBy(() -> itemService.update(1L, itemDetails))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品名称 'OtherItem' 已存在");
        }
    }

    // ==================== deleteById 测试 ====================

    @Nested
    @DisplayName("deleteById 方法")
    class DeleteById {

        @Test
        @DisplayName("物品存在时删除")
        void should_deleteItem_when_itemExists() {
            // Arrange
            when(itemRepository.existsById(1L)).thenReturn(true);

            // Act
            itemService.deleteById(1L);

            // Assert
            verify(itemRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("物品不存在时抛出异常")
        void should_throwException_when_itemNotFound() {
            // Arrange
            when(itemRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> itemService.deleteById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品不存在，ID: 999");
            verify(itemRepository, never()).deleteById(any());
        }
    }

    // ==================== searchByKeyword 测试 ====================

    @Nested
    @DisplayName("searchByKeyword 方法")
    class SearchByKeyword {

        @Test
        @DisplayName("有关键词时调用搜索")
        void should_searchByKeyword_when_keywordProvided() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.searchByKeyword("test")).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.searchByKeyword("test");

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Test Item");
            verify(itemRepository).searchByKeyword("test");
        }

        @Test
        @DisplayName("关键词为null时返回全部")
        void should_returnAll_when_keywordIsNull() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findAll()).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.searchByKeyword(null);

            // Assert
            assertThat(result).hasSize(1);
            verify(itemRepository).findAll();
            verify(itemRepository, never()).searchByKeyword(any());
        }

        @Test
        @DisplayName("关键词为空字符串时返回全部")
        void should_returnAll_when_keywordIsBlank() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findAll()).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.searchByKeyword("   ");

            // Assert
            assertThat(result).hasSize(1);
            verify(itemRepository).findAll();
            verify(itemRepository, never()).searchByKeyword(any());
        }
    }

    // ==================== searchByKeywordAndUserId 测试 ====================

    @Nested
    @DisplayName("searchByKeywordAndUserId 方法")
    class SearchByKeywordAndUserId {

        @Test
        @DisplayName("有关键词时按关键词和用户搜索")
        void should_searchByKeywordAndUserId_when_keywordProvided() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.searchByKeywordAndUserId("test", 1L)).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.searchByKeywordAndUserId("test", 1L);

            // Assert
            assertThat(result).hasSize(1);
            verify(itemRepository).searchByKeywordAndUserId("test", 1L);
        }

        @Test
        @DisplayName("关键词为null时按用户ID查找")
        void should_findByUserId_when_keywordIsNull() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findByUserId(1L)).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.searchByKeywordAndUserId(null, 1L);

            // Assert
            assertThat(result).hasSize(1);
            verify(itemRepository).findByUserId(1L);
            verify(itemRepository, never()).searchByKeywordAndUserId(any(), any());
        }
    }

    // ==================== findByCategory 测试 ====================

    @Nested
    @DisplayName("findByCategory 方法")
    class FindByCategory {

        @Test
        @DisplayName("按分类查找物品")
        void should_returnItemsByCategory() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findByCategory("Electronics")).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.findByCategory("Electronics");

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategory()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("分类无物品时返回空列表")
        void should_returnEmptyList_when_categoryNotFound() {
            // Arrange
            when(itemRepository.findByCategory("NonExistent")).thenReturn(List.of());

            // Act
            List<Item> result = itemService.findByCategory("NonExistent");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // ==================== findByUserId 测试 ====================

    @Nested
    @DisplayName("findByUserId 方法")
    class FindByUserId {

        @Test
        @DisplayName("按用户ID查找物品")
        void should_returnItemsByUserId() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findByUserId(1L)).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.findByUserId(1L);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUserId()).isEqualTo(1L);
        }
    }

    // ==================== findLowStockItems 测试 ====================

    @Nested
    @DisplayName("findLowStockItems 方法")
    class FindLowStockItems {

        @Test
        @DisplayName("返回库存低于阈值的物品")
        void should_returnItemsWithQuantityLessThanThreshold() {
            // Arrange
            Item item = createDefaultItem();
            when(itemRepository.findByQuantityLessThan(5)).thenReturn(List.of(item));

            // Act
            List<Item> result = itemService.findLowStockItems(5);

            // Assert
            assertThat(result).hasSize(1);
            verify(itemRepository).findByQuantityLessThan(5);
        }
    }

    // ==================== getAllCategories 测试 ====================

    @Nested
    @DisplayName("getAllCategories 方法")
    class GetAllCategories {

        @Test
        @DisplayName("返回所有分类列表")
        void should_returnAllCategories() {
            // Arrange
            when(itemRepository.findAllCategories()).thenReturn(List.of("Electronics", "Books", "Clothing"));

            // Act
            List<String> result = itemService.getAllCategories();

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly("Electronics", "Books", "Clothing");
        }

        @Test
        @DisplayName("无分类时返回空列表")
        void should_returnEmptyList_when_noCategories() {
            // Arrange
            when(itemRepository.findAllCategories()).thenReturn(List.of());

            // Act
            List<String> result = itemService.getAllCategories();

            // Assert
            assertThat(result).isEmpty();
        }
    }
}