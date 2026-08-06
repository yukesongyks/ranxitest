package com.example.myapp.services;

import com.example.myapp.models.Item;
import com.example.myapp.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ItemService 单元测试
 * 遵循 FIRST 原则 (Fast, Independent, Repeatable, Self-Validating, Timely)
 * 使用 Mockito 隔离 Repository 依赖，不依赖真实数据库
 */
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
        sampleItem = new Item();
        sampleItem.setId(1L);
        sampleItem.setName("测试商品");
        sampleItem.setDescription("这是一个测试商品");
        sampleItem.setPrice(new BigDecimal("99.99"));
        sampleItem.setQuantity(100);
        sampleItem.setCategory("电子产品");
        sampleItem.setUserId(1L);
        sampleItem.setCreatedAt(LocalDateTime.now());
        sampleItem.setUpdatedAt(LocalDateTime.now());
    }

    private Item createItem(Long id, String name, BigDecimal price, int quantity, String category) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setCategory(category);
        item.setUserId(1L);
        return item;
    }

    // ======================== getItemById ========================

    @Nested
    @DisplayName("getItemById 方法")
    class GetItemByIdTests {

        @Test
        @DisplayName("根据存在的 ID 获取商品应返回商品")
        void shouldReturnItemWhenIdExists() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

            Optional<Item> result = itemService.getItemById(1L);

            assertTrue(result.isPresent());
            assertEquals("测试商品", result.get().getName());
            assertEquals(new BigDecimal("99.99"), result.get().getPrice());
            verify(itemRepository).findById(1L);
        }

        @Test
        @DisplayName("根据不存在的 ID 获取商品应返回空 Optional")
        void shouldReturnEmptyWhenIdNotFound() {
            when(itemRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Item> result = itemService.getItemById(999L);

            assertFalse(result.isPresent());
            verify(itemRepository).findById(999L);
        }
    }

    // ======================== getItemsByUserId ========================

    @Nested
    @DisplayName("getItemsByUserId 方法")
    class GetItemsByUserIdTests {

        @Test
        @DisplayName("用户有商品时返回商品列表")
        void shouldReturnItemsWhenUserHasItems() {
            List<Item> items = Arrays.asList(
                    createItem(1L, "商品A", new BigDecimal("10.00"), 5, "书籍"),
                    createItem(2L, "商品B", new BigDecimal("20.00"), 3, "电子产品")
            );
            when(itemRepository.findByUserId(1L)).thenReturn(items);

            List<Item> result = itemService.getItemsByUserId(1L);

            assertEquals(2, result.size());
            assertEquals("商品A", result.get(0).getName());
            assertEquals("商品B", result.get(1).getName());
            verify(itemRepository).findByUserId(1L);
        }

        @Test
        @DisplayName("用户没有商品时返回空列表")
        void shouldReturnEmptyListWhenUserHasNoItems() {
            when(itemRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

            List<Item> result = itemService.getItemsByUserId(999L);

            assertTrue(result.isEmpty());
            verify(itemRepository).findByUserId(999L);
        }
    }

    // ======================== getAllItems ========================

    @Nested
    @DisplayName("getAllItems 方法")
    class GetAllItemsTests {

        @Test
        @DisplayName("获取所有商品分页结果")
        void shouldReturnPagedItems() {
            List<Item> items = Arrays.asList(
                    createItem(1L, "商品A", new BigDecimal("10.00"), 5, "书籍"),
                    createItem(2L, "商品B", new BigDecimal("20.00"), 3, "电子产品")
            );
            Page<Item> page = new PageImpl<>(items);
            when(itemRepository.findAll(any(Pageable.class))).thenReturn(page);

            Page<Item> result = itemService.getAllItems(PageRequest.of(0, 10));

            assertEquals(2, result.getTotalElements());
            assertEquals("商品A", result.getContent().get(0).getName());
            verify(itemRepository).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("无商品时返回空分页")
        void shouldReturnEmptyPageWhenNoItems() {
            Page<Item> emptyPage = new PageImpl<>(Collections.emptyList());
            when(itemRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            Page<Item> result = itemService.getAllItems(PageRequest.of(0, 10));

            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
            verify(itemRepository).findAll(any(Pageable.class));
        }
    }

    // ======================== createItem ========================

    @Nested
    @DisplayName("createItem 方法")
    class CreateItemTests {

        @Test
        @DisplayName("创建商品成功应返回保存后的商品")
        void shouldCreateItemSuccessfully() {
            Item newItem = new Item();
            newItem.setName("新商品");
            newItem.setPrice(new BigDecimal("49.99"));
            newItem.setQuantity(10);
            newItem.setCategory("家居");
            newItem.setUserId(1L);

            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
                Item item = invocation.getArgument(0);
                item.setId(100L);
                return item;
            });

            Item result = itemService.createItem(newItem);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals("新商品", result.getName());
            assertEquals(new BigDecimal("49.99"), result.getPrice());
            assertEquals(10, result.getQuantity());
            verify(itemRepository).save(any(Item.class));
        }

        @Test
        @DisplayName("创建价格为 0 的商品应允许")
        void shouldAllowZeroPriceItem() {
            Item newItem = new Item();
            newItem.setName("免费商品");
            newItem.setPrice(BigDecimal.ZERO);
            newItem.setQuantity(1);
            newItem.setUserId(1L);

            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
                Item item = invocation.getArgument(0);
                item.setId(200L);
                return item;
            });

            Item result = itemService.createItem(newItem);

            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.getPrice());
            verify(itemRepository).save(any(Item.class));
        }

        @Test
        @DisplayName("创建数量为 0 的商品应允许")
        void shouldAllowZeroQuantityItem() {
            Item newItem = new Item();
            newItem.setName("缺货商品");
            newItem.setPrice(new BigDecimal("5.00"));
            newItem.setQuantity(0);
            newItem.setUserId(1L);

            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
                Item item = invocation.getArgument(0);
                item.setId(300L);
                return item;
            });

            Item result = itemService.createItem(newItem);

            assertEquals(0, result.getQuantity());
            verify(itemRepository).save(any(Item.class));
        }
    }

    // ======================== updateItem ========================

    @Nested
    @DisplayName("updateItem 方法")
    class UpdateItemTests {

        @Test
        @DisplayName("更新存在的商品应返回更新后的商品")
        void shouldUpdateExistingItem() {
            Item updatedDetails = new Item();
            updatedDetails.setName("更新商品");
            updatedDetails.setPrice(new BigDecimal("59.99"));
            updatedDetails.setQuantity(50);
            updatedDetails.setCategory("更新分类");

            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Item result = itemService.updateItem(1L, updatedDetails);

            assertNotNull(result);
            assertEquals("更新商品", result.getName());
            assertEquals(new BigDecimal("59.99"), result.getPrice());
            assertEquals(50, result.getQuantity());
            assertEquals("更新分类", result.getCategory());
            verify(itemRepository).findById(1L);
            verify(itemRepository).save(any(Item.class));
        }

        @Test
        @DisplayName("更新不存在的商品应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenItemNotFound() {
            Item updatedDetails = new Item();
            updatedDetails.setName("不存在");

            when(itemRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> itemService.updateItem(999L, updatedDetails)
            );

            assertTrue(exception.getMessage().contains("商品不存在"));
            verify(itemRepository).findById(999L);
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        @DisplayName("部分更新时未提供的字段保持原值")
        void shouldKeepOriginalValuesForNullFields() {
            Item updatedDetails = new Item();
            updatedDetails.setName("只改名称");
            updatedDetails.setPrice(null);
            updatedDetails.setQuantity(null);
            updatedDetails.setCategory(null);

            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Item result = itemService.updateItem(1L, updatedDetails);

            assertEquals("只改名称", result.getName());
            assertEquals(new BigDecimal("99.99"), result.getPrice());
            assertEquals(100, result.getQuantity());
            assertEquals("电子产品", result.getCategory());
        }
    }

    // ======================== deleteItem ========================

    @Nested
    @DisplayName("deleteItem 方法")
    class DeleteItemTests {

        @Test
        @DisplayName("删除存在的商品应成功")
        void shouldDeleteExistingItem() {
            when(itemRepository.existsById(1L)).thenReturn(true);
            doNothing().when(itemRepository).deleteById(1L);

            itemService.deleteItem(1L);

            verify(itemRepository).existsById(1L);
            verify(itemRepository).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的商品应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenDeletingNonexistentItem() {
            when(itemRepository.existsById(999L)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> itemService.deleteItem(999L)
            );

            assertTrue(exception.getMessage().contains("商品不存在"));
            verify(itemRepository).existsById(999L);
            verify(itemRepository, never()).deleteById(anyLong());
        }
    }

    // ======================== searchItems ========================

    @Nested
    @DisplayName("searchItems 方法")
    class SearchItemsTests {

        @Test
        @DisplayName("搜索关键词有匹配结果时返回商品列表")
        void shouldReturnMatchingItems() {
            List<Item> items = Collections.singletonList(sampleItem);
            when(itemRepository.findByNameContainingIgnoreCase("测试")).thenReturn(items);

            List<Item> result = itemService.searchItems("测试");

            assertEquals(1, result.size());
            assertEquals("测试商品", result.get(0).getName());
            verify(itemRepository).findByNameContainingIgnoreCase("测试");
        }

        @Test
        @DisplayName("搜索无匹配结果时返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            when(itemRepository.findByNameContainingIgnoreCase("不存在")).thenReturn(Collections.emptyList());

            List<Item> result = itemService.searchItems("不存在");

            assertTrue(result.isEmpty());
            verify(itemRepository).findByNameContainingIgnoreCase("不存在");
        }
    }

    // ======================== findByCategory ========================

    @Nested
    @DisplayName("findByCategory 方法")
    class FindByCategoryTests {

        @Test
        @DisplayName("按分类查找有结果时返回商品列表")
        void shouldReturnItemsByCategory() {
            List<Item> items = Arrays.asList(
                    createItem(1L, "手机", new BigDecimal("5000.00"), 10, "电子产品"),
                    createItem(2L, "平板", new BigDecimal("3000.00"), 5, "电子产品")
            );
            when(itemRepository.findByCategory("电子产品")).thenReturn(items);

            List<Item> result = itemService.findByCategory("电子产品");

            assertEquals(2, result.size());
            assertEquals("手机", result.get(0).getName());
            verify(itemRepository).findByCategory("电子产品");
        }

        @Test
        @DisplayName("按不存在的分类查找应返回空列表")
        void shouldReturnEmptyListWhenCategoryNotFound() {
            when(itemRepository.findByCategory("不存在的分类")).thenReturn(Collections.emptyList());

            List<Item> result = itemService.findByCategory("不存在的分类");

            assertTrue(result.isEmpty());
            verify(itemRepository).findByCategory("不存在的分类");
        }
    }

    // ======================== findByQuantityLessThan ========================

    @Nested
    @DisplayName("findByQuantityLessThan 方法")
    class FindByQuantityLessThanTests {

        @Test
        @DisplayName("查找低库存商品应返回列表")
        void shouldReturnLowStockItems() {
            List<Item> items = Collections.singletonList(
                    createItem(1L, "缺货商品", new BigDecimal("10.00"), 2, "书籍")
            );
            when(itemRepository.findByQuantityLessThan(5)).thenReturn(items);

            List<Item> result = itemService.findByQuantityLessThan(5);

            assertEquals(1, result.size());
            assertEquals(2, result.get(0).getQuantity());
            verify(itemRepository).findByQuantityLessThan(5);
        }

        @Test
        @DisplayName("没有低库存商品时返回空列表")
        void shouldReturnEmptyListWhenNoLowStockItems() {
            when(itemRepository.findByQuantityLessThan(5)).thenReturn(Collections.emptyList());

            List<Item> result = itemService.findByQuantityLessThan(5);

            assertTrue(result.isEmpty());
            verify(itemRepository).findByQuantityLessThan(5);
        }
    }

    // ======================== getAllCategories ========================

    @Nested
    @DisplayName("getAllCategories 方法")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("获取所有分类应返回去重后的分类列表")
        void shouldReturnDistinctCategories() {
            List<String> categories = Arrays.asList("电子产品", "书籍", "家居");
            when(itemRepository.findAllCategories()).thenReturn(categories);

            List<String> result = itemService.getAllCategories();

            assertEquals(3, result.size());
            assertTrue(result.contains("电子产品"));
            assertTrue(result.contains("书籍"));
            assertTrue(result.contains("家居"));
            verify(itemRepository).findAllCategories();
        }

        @Test
        @DisplayName("无分类时返回空列表")
        void shouldReturnEmptyListWhenNoCategories() {
            when(itemRepository.findAllCategories()).thenReturn(Collections.emptyList());

            List<String> result = itemService.getAllCategories();

            assertTrue(result.isEmpty());
            verify(itemRepository).findAllCategories();
        }
    }
}