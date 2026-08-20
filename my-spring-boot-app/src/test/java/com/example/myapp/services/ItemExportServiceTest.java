package com.example.myapp.services;

import com.example.myapp.docgen.DocgenExportException;
import com.example.myapp.docgen.TxtRow;
import com.example.myapp.models.Item;
import com.example.myapp.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * ItemExportService 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class ItemExportServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemExportService itemExportService;

    // ==================== buildRows 测试 ====================

    @Test
    void should_buildRows_when_itemsExist() {
        // Arrange
        Item apple = new Item();
        apple.setId(1L);
        apple.setName("苹果");
        apple.setDescription("红富士");
        apple.setCategory("水果");
        apple.setQuantity(10);
        apple.setPrice(new BigDecimal("5.50"));
        Item banana = new Item();
        banana.setId(2L);
        banana.setName("香蕉");
        banana.setDescription("进口香蕉");
        banana.setCategory("水果");
        banana.setQuantity(20);
        banana.setPrice(new BigDecimal("3.20"));
        when(itemRepository.findAll()).thenReturn(Arrays.asList(apple, banana));

        // Act
        List<TxtRow> rows = itemExportService.buildRows();

        // Assert
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getCells()).containsExactly("1", "苹果", "红富士", "5.50");
        assertThat(rows.get(1).getCells()).containsExactly("2", "香蕉", "进口香蕉", "3.20");
    }

    @Test
    void should_returnEmptyRows_when_noItems() {
        // Arrange
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<TxtRow> rows = itemExportService.buildRows();

        // Assert
        assertThat(rows).isEmpty();
    }

    @Test
    void should_throwException_when_repositoryFails() {
        // Arrange
        when(itemRepository.findAll()).thenThrow(new RuntimeException("数据库不可用"));

        // Act & Assert
        assertThatThrownBy(() -> itemExportService.buildRows())
                .isInstanceOf(DocgenExportException.class)
                .extracting(e -> ((DocgenExportException) e).getErrorCode())
                .isEqualTo("DOCGEN_001");
    }
}