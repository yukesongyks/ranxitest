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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ItemService} 单元测试。
 *
 * <p>覆盖核心业务逻辑：新增/更新时的唯一性校验、删除前存在性校验、
 * 搜索关键词的空值 fallback、低库存查询与分类列表查询。
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
        sampleItem = new Item("测试物品", "描述", "电子", 10, new BigDecimal("99.99"));
        sampleItem.setId(1L);
    }

    // ------------------------------------------------------------------
    // findAll
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findAll: 委托 Repository 返回全部物品")
    void findAll_shouldDelegateToRepository() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(sampleItem, new Item()));

        List<Item> result = itemService.findAll();

        assertThat(result).hasSize(2).contains(sampleItem);
        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("findAll: 仓库无数据时返回空列表")
    void findAll_shouldReturnEmptyListWhenNoData() {
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        List<Item> result = itemService.findAll();

        assertThat(result).isEmpty();
    }

    // ------------------------------------------------------------------
    // findById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findById: 存在时返回 Optional 包含物品")
    void findById_shouldReturnItemWhenExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        Optional<Item> result = itemService.findById(1L);

        assertThat(result).isPresent().contains(sampleItem);
    }

    @Test
    @DisplayName("findById: 不存在时返回空 Optional")
    void findById_shouldReturnEmptyWhenNotExists() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Item> result = itemService.findById(999L);

        assertThat(result).isEmpty();
    }

    // ------------------------------------------------------------------
    // findByName
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findByName: 委托 Repository 按名称查询")
    void findByName_shouldDelegateToRepository() {
        when(itemRepository.findByName("测试物品")).thenReturn(Optional.of(sampleItem));

        Optional<Item> result = itemService.findByName("测试物品");

        assertThat(result).isPresent().contains(sampleItem);
    }

    // ------------------------------------------------------------------
    // save (新增)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("save: 新增物品名称不存在时正常保存")
    void save_shouldPersistWhenNameNotDuplicated() {
        Item newItem = new Item("新物品", "描述", "电子", 5, new BigDecimal("10.00"));
        when(itemRepository.existsByName("新物品")).thenReturn(false);
        when(itemRepository.save(newItem)).thenReturn(newItem);

        Item result = itemService.save(newItem);

        assertThat(result).isSameAs(newItem);
        verify(itemRepository).save(newItem);
    }

    @Test
    @DisplayName("save: 新增时名称已存在则抛 IllegalArgumentException")
    void save_shouldThrowWhenNameAlreadyExists() {
        Item duplicate = new Item("测试物品", "描述", "电子", 5, new BigDecimal("10.00"));
        when(itemRepository.existsByName("测试物品")).thenReturn(true);

        assertThatThrownBy(() -> itemService.save(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("测试物品")
                .hasMessageContaining("已存在");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("save: 已有 ID 的物品跳过唯一性校验直接保存")
    void save_shouldSkipUniquenessCheckWhenIdNotNull() {
        sampleItem.setName("已有ID物品");
        when(itemRepository.save(sampleItem)).thenReturn(sampleItem);

        Item result = itemService.save(sampleItem);

        assertThat(result).isSameAs(sampleItem);
        verify(itemRepository, never()).existsByName(any());
    }

    @Test
    @DisplayName("save: Repository 抛 DataIntegrityViolationException 时转换为 IllegalArgumentException")
    void save_shouldWrapDataIntegrityViolationAsIllegalArgument() {
        Item newItem = new Item("冲突物品", "描述", "电子", 5, new BigDecimal("10.00"));
        when(itemRepository.existsByName("冲突物品")).thenReturn(false);
        when(itemRepository.save(newItem))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        assertThatThrownBy(() -> itemService.save(newItem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("冲突物品");

        verify(itemRepository).save(newItem);
    }

    // ------------------------------------------------------------------
    // update
    // ------------------------------------------------------------------

    @Test
    @DisplayName("update: 名称未变且物品存在时正常更新")
    void update_shouldUpdateWhenNameUnchanged() {
        Item details = new Item("测试物品", "新描述", "新分类", 20, new BigDecimal("88.88"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(sampleItem)).thenReturn(sampleItem);

        Item result = itemService.update(1L, details);

        assertThat(result).isSameAs(sampleItem);
        assertThat(sampleItem.getDescription()).isEqualTo("新描述");
        assertThat(sampleItem.getCategory()).isEqualTo("新分类");
        assertThat(sampleItem.getQuantity()).isEqualTo(20);
        verify(itemRepository).save(sampleItem);
        verify(itemRepository, never()).findByNameForUpdate(any());
    }

    @Test
    @DisplayName("update: 名称变更且无冲突时正常更新")
    void update_shouldUpdateWhenNameChangedAndNoConflict() {
        Item details = new Item("改名后", "描述", "电子", 10, new BigDecimal("99.99"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.findByNameForUpdate("改名后")).thenReturn(Optional.empty());
        when(itemRepository.save(sampleItem)).thenReturn(sampleItem);

        Item result = itemService.update(1L, details);

        assertThat(result.getName()).isEqualTo("改名后");
        verify(itemRepository).findByNameForUpdate("改名后");
        verify(itemRepository).save(sampleItem);
    }

    @Test
    @DisplayName("update: 物品不存在时抛 IllegalArgumentException")
    void update_shouldThrowWhenItemNotFound() {
        Item details = new Item("改名", "描述", "电子", 10, new BigDecimal("99.99"));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(999L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("物品不存在");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("update: 名称变更为已存在名称时抛 IllegalArgumentException")
    void update_shouldThrowWhenNewNameConflicts() {
        Item details = new Item("占用名称", "描述", "电子", 10, new BigDecimal("99.99"));
        Item existingOwner = new Item("占用名称", "其他", "电子", 1, new BigDecimal("1.00"));
        existingOwner.setId(2L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.findByNameForUpdate("占用名称")).thenReturn(Optional.of(existingOwner));

        assertThatThrownBy(() -> itemService.update(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("占用名称");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("update: 保存时 Repository 抛 DataIntegrityViolationException 时转换为 IllegalArgumentException")
    void update_shouldWrapDataIntegrityViolationAsIllegalArgument() {
        Item details = new Item("测试物品", "新描述", "新分类", 20, new BigDecimal("88.88"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(sampleItem))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        assertThatThrownBy(() -> itemService.update(1L, details))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ------------------------------------------------------------------
    // deleteById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("deleteById: 物品存在时正常删除")
    void deleteById_shouldDeleteWhenExists() {
        when(itemRepository.existsById(1L)).thenReturn(true);

        itemService.deleteById(1L);

        verify(itemRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: 物品不存在时抛 IllegalArgumentException")
    void deleteById_shouldThrowWhenNotExists() {
        when(itemRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> itemService.deleteById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("物品不存在");

        verify(itemRepository, never()).deleteById(any());
    }

    // ------------------------------------------------------------------
    // searchByKeyword
    // ------------------------------------------------------------------

    @Test
    @DisplayName("searchByKeyword: 关键词为 null 时 fallback 到 findAll")
    void searchByKeyword_shouldFallbackToFindAllWhenKeywordNull() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.searchByKeyword(null);

        assertThat(result).containsExactly(sampleItem);
        verify(itemRepository, never()).searchByKeyword(any());
    }

    @Test
    @DisplayName("searchByKeyword: 关键词为空白时 fallback 到 findAll")
    void searchByKeyword_shouldFallbackToFindAllWhenKeywordBlank() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.searchByKeyword("   ");

        assertThat(result).containsExactly(sampleItem);
        verify(itemRepository, never()).searchByKeyword(any());
    }

    @Test
    @DisplayName("searchByKeyword: 关键词非空时委托 Repository 搜索（trim 后）")
    void searchByKeyword_shouldDelegateToRepositoryWhenKeywordPresent() {
        when(itemRepository.searchByKeyword("手机"))
                .thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.searchByKeyword("  手机  ");

        assertThat(result).containsExactly(sampleItem);
        verify(itemRepository).searchByKeyword("手机");
    }

    // ------------------------------------------------------------------
    // searchByKeywordAndUserId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("searchByKeywordAndUserId: 关键词为 null 时返回用户全部物品")
    void searchByKeywordAndUserId_shouldFindByUserIdWhenKeywordNull() {
        when(itemRepository.findByUserId(100L))
                .thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.searchByKeywordAndUserId(null, 100L);

        assertThat(result).containsExactly(sampleItem);
        verify(itemRepository, never()).searchByKeywordAndUserId(any(), any());
    }

    @Test
    @DisplayName("searchByKeywordAndUserId: 关键词为空白时返回用户全部物品")
    void searchByKeywordAndUserId_shouldFindByUserIdWhenKeywordBlank() {
        when(itemRepository.findByUserId(100L))
                .thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.searchByKeywordAndUserId("  ", 100L);

        assertThat(result).containsExactly(sampleItem);
        verify(itemRepository, never()).searchByKeywordAndUserId(any(), any());
    }

    @Test
    @DisplayName("searchByKeywordAndUserId: 关键词非空时委托 Repository 按用户+关键词搜索")
    void searchByKeywordAndUserId_shouldDelegateWhenKeywordPresent() {
        when(itemRepository.searchByKeywordAndUserId("手机", 100L))
                .thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.searchByKeywordAndUserId(" 手机 ", 100L);

        assertThat(result).containsExactly(sampleItem);
        verify(itemRepository).searchByKeywordAndUserId("手机", 100L);
    }

    // ------------------------------------------------------------------
    // findByCategory / findByUserId / findLowStockItems / getAllCategories
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findByCategory: 委托 Repository 按分类查询")
    void findByCategory_shouldDelegateToRepository() {
        when(itemRepository.findByCategory("电子"))
                .thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.findByCategory("电子");

        assertThat(result).containsExactly(sampleItem);
    }

    @Test
    @DisplayName("findByUserId: 委托 Repository 按用户 ID 查询")
    void findByUserId_shouldDelegateToRepository() {
        when(itemRepository.findByUserId(100L))
                .thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.findByUserId(100L);

        assertThat(result).containsExactly(sampleItem);
    }

    @Test
    @DisplayName("findLowStockItems: 委托 Repository 查询低于阈值的物品")
    void findLowStockItems_shouldDelegateToRepository() {
        when(itemRepository.findByQuantityLessThan(5))
                .thenReturn(Collections.singletonList(sampleItem));

        List<Item> result = itemService.findLowStockItems(5);

        assertThat(result).containsExactly(sampleItem);
    }

    @Test
    @DisplayName("getAllCategories: 委托 Repository 查询去重分类列表")
    void getAllCategories_shouldDelegateToRepository() {
        List<String> categories = Arrays.asList("电子", "食品", "图书");
        when(itemRepository.findAllCategories()).thenReturn(categories);

        List<String> result = itemService.getAllCategories();

        assertThat(result).containsExactly("电子", "食品", "图书");
    }
}
