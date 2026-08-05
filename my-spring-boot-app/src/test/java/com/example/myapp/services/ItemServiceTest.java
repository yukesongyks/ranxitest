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
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Arrays;
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
 * 使用 Mockito 隔离 {@link ItemRepository}，验证 Service 层业务逻辑分支，
 * 包括正常路径、参数校验、唯一性校验、异常转换等。
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
        sampleItem = new Item("测试物品", "描述", "电子产品", 10, new BigDecimal("99.99"));
        sampleItem.setId(1L);
    }

    // ------------------------------------------------------------------
    // findAll
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("findAll 查询全部物品")
    class FindAll {

        @Test
        @DisplayName("仓库返回多条记录时，应原样返回")
        void shouldReturnAllItems() {
            // given
            when(itemRepository.findAll()).thenReturn(Arrays.asList(sampleItem, new Item()));

            // when
            List<Item> result = itemService.findAll();

            // then
            assertThat(result).hasSize(2);
            verify(itemRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("仓库无数据时，应返回空列表")
        void shouldReturnEmptyListWhenNoData() {
            when(itemRepository.findAll()).thenReturn(Collections.emptyList());

            List<Item> result = itemService.findAll();

            assertThat(result).isEmpty();
        }
    }

    // ------------------------------------------------------------------
    // findById
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("findById 按 ID 查询物品")
    class FindById {

        @Test
        @DisplayName("物品存在时，应返回 Optional 含值")
        void shouldReturnItemWhenExists() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

            Optional<Item> result = itemService.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("测试物品");
        }

        @Test
        @DisplayName("物品不存在时，应返回空 Optional")
        void shouldReturnEmptyWhenNotExists() {
            when(itemRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Item> result = itemService.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    // ------------------------------------------------------------------
    // findByName
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findByName 按名称查询物品")
    void findByName_shouldDelegateToRepository() {
        when(itemRepository.findByName("测试物品")).thenReturn(Optional.of(sampleItem));

        Optional<Item> result = itemService.findByName("测试物品");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    // ------------------------------------------------------------------
    // save
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("save 新增物品")
    class Save {

        @Test
        @DisplayName("新增物品且名称不重复时，应保存成功")
        void shouldSaveWhenNameNotDuplicated() {
            Item newItem = new Item("新物品", "新描述", "食品", 5, new BigDecimal("10.00"));
            when(itemRepository.existsByName("新物品")).thenReturn(false);
            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Item result = itemService.save(newItem);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("新物品");
            verify(itemRepository, times(1)).save(newItem);
        }

        @Test
        @DisplayName("新增物品且名称已存在时，应抛出 IllegalArgumentException")
        void shouldThrowWhenNameAlreadyExists() {
            Item duplicate = new Item("测试物品", "描述", "电子产品", 10, new BigDecimal("99.99"));
            when(itemRepository.existsByName("测试物品")).thenReturn(true);

            assertThatThrownBy(() -> itemService.save(duplicate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品名称 '测试物品' 已存在");

            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        @DisplayName("保存时发生 DataIntegrityViolationException 应转换为 IllegalArgumentException")
        void shouldConvertDataIntegrityViolation() {
            Item item = new Item("冲突物品", "描述", "电子产品", 1, new BigDecimal("1.00"));
            when(itemRepository.existsByName("冲突物品")).thenReturn(false);
            when(itemRepository.save(any(Item.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint"));

            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("已存在");
        }

        @Test
        @DisplayName("物品已有 ID（非新增）时，应跳过名称重复检查直接保存")
        void shouldSkipExistsCheckWhenIdNotNull() {
            sampleItem.setId(5L);
            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Item result = itemService.save(sampleItem);

            assertThat(result).isNotNull();
            verify(itemRepository, never()).existsByName(anyString());
        }
    }

    // ------------------------------------------------------------------
    // update
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("update 更新物品")
    class Update {

        @Test
        @DisplayName("物品存在且名称未变时，应更新字段并保存")
        void shouldUpdateWhenNameUnchanged() {
            Item existing = new Item("原名称", "原描述", "电子产品", 5, new BigDecimal("50.00"));
            existing.setId(1L);
            when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Item details = new Item("原名称", "新描述", "电子产品", 20, new BigDecimal("60.00"));
            Item result = itemService.update(1L, details);

            assertThat(result.getDescription()).isEqualTo("新描述");
            assertThat(result.getQuantity()).isEqualTo(20);
            assertThat(result.getPrice()).isEqualByComparingTo("60.00");
            verify(itemRepository, never()).findByNameForUpdate(anyString());
        }

        @Test
        @DisplayName("物品不存在时，应抛出 IllegalArgumentException")
        void shouldThrowWhenItemNotFound() {
            when(itemRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> itemService.update(999L, new Item()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品不存在，ID: 999");
        }

        @Test
        @DisplayName("新名称已被其他物品占用时，应抛出 IllegalArgumentException")
        void shouldThrowWhenNewNameTaken() {
            Item existing = new Item("原名称", "描述", "电子产品", 5, new BigDecimal("50.00"));
            existing.setId(1L);
            when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(itemRepository.findByNameForUpdate("重复名称"))
                    .thenReturn(Optional.of(new Item("重复名称", "", "", 1, BigDecimal.ZERO)));

            Item details = new Item("重复名称", "描述", "电子产品", 5, new BigDecimal("50.00"));
            assertThatThrownBy(() -> itemService.update(1L, details))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品名称 '重复名称' 已存在");
        }

        @Test
        @DisplayName("更新时发生 DataIntegrityViolationException 应转换为 IllegalArgumentException")
        void shouldConvertViolationOnUpdate() {
            Item existing = new Item("原名称", "描述", "电子产品", 5, new BigDecimal("50.00"));
            existing.setId(1L);
            when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(itemRepository.save(any(Item.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint"));

            Item details = new Item("原名称", "新描述", "电子产品", 5, new BigDecimal("50.00"));
            assertThatThrownBy(() -> itemService.update(1L, details))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("已存在");
        }
    }

    // ------------------------------------------------------------------
    // deleteById
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("deleteById 删除物品")
    class DeleteById {

        @Test
        @DisplayName("物品存在时，应执行删除")
        void shouldDeleteWhenExists() {
            when(itemRepository.existsById(1L)).thenReturn(true);

            itemService.deleteById(1L);

            verify(itemRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("物品不存在时，应抛出 IllegalArgumentException 且不执行删除")
        void shouldThrowWhenNotExists() {
            when(itemRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> itemService.deleteById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("物品不存在，ID: 999");

            verify(itemRepository, never()).deleteById(anyLong());
        }
    }

    // ------------------------------------------------------------------
    // searchByKeyword
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("searchByKeyword 关键词搜索")
    class SearchByKeyword {

        @Test
        @DisplayName("关键词为 null 时，应返回全部物品")
        void shouldReturnAllWhenKeywordIsNull() {
            when(itemRepository.findAll()).thenReturn(Collections.singletonList(sampleItem));

            List<Item> result = itemService.searchByKeyword(null);

            assertThat(result).hasSize(1);
            verify(itemRepository, never()).searchByKeyword(anyString());
        }

        @Test
        @DisplayName("关键词为空白时，应返回全部物品")
        void shouldReturnAllWhenKeywordIsBlank() {
            when(itemRepository.findAll()).thenReturn(Collections.singletonList(sampleItem));

            List<Item> result = itemService.searchByKeyword("   ");

            assertThat(result).hasSize(1);
            verify(itemRepository, never()).searchByKeyword(anyString());
        }

        @Test
        @DisplayName("关键词有效时，应调用仓库搜索并自动 trim")
        void shouldSearchWhenKeywordValid() {
            when(itemRepository.searchByKeyword("手机")).thenReturn(Collections.singletonList(sampleItem));

            List<Item> result = itemService.searchByKeyword("  手机  ");

            assertThat(result).hasSize(1);
            verify(itemRepository, times(1)).searchByKeyword("手机");
        }
    }

    // ------------------------------------------------------------------
    // searchByKeywordAndUserId
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("searchByKeywordAndUserId 按用户关键词搜索")
    class SearchByKeywordAndUserId {

        @Test
        @DisplayName("关键词为空时，应返回该用户所有物品")
        void shouldFindByUserIdWhenKeywordBlank() {
            when(itemRepository.findByUserId(10L)).thenReturn(Collections.singletonList(sampleItem));

            List<Item> result = itemService.searchByKeywordAndUserId("  ", 10L);

            assertThat(result).hasSize(1);
            verify(itemRepository, never()).searchByKeywordAndUserId(anyString(), anyLong());
        }

        @Test
        @DisplayName("关键词有效时，应调用按用户搜索并自动 trim")
        void shouldSearchWhenKeywordValid() {
            when(itemRepository.searchByKeywordAndUserId("手机", 10L))
                    .thenReturn(Collections.singletonList(sampleItem));

            List<Item> result = itemService.searchByKeywordAndUserId("手机 ", 10L);

            assertThat(result).hasSize(1);
            verify(itemRepository, times(1)).searchByKeywordAndUserId("手机", 10L);
        }
    }

    // ------------------------------------------------------------------
    // findByCategory / findByUserId / findLowStockItems / getAllCategories
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findByCategory 应委托仓库按分类查询")
    void findByCategory_shouldDelegate() {
        when(itemRepository.findByCategory("电子产品")).thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.findByCategory("电子产品");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findByUserId 应委托仓库按用户 ID 查询")
    void findByUserId_shouldDelegate() {
        when(itemRepository.findByUserId(1L)).thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.findByUserId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findLowStockItems 应委托仓库按库存阈值查询")
    void findLowStockItems_shouldDelegate() {
        when(itemRepository.findByQuantityLessThan(5)).thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.findLowStockItems(5);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getAllCategories 应委托仓库查询所有分类")
    void getAllCategories_shouldDelegate() {
        when(itemRepository.findAllCategories()).thenReturn(Arrays.asList("电子产品", "食品"));

        List<String> result = itemService.getAllCategories();

        assertThat(result).containsExactly("电子产品", "食品");
    }
}
