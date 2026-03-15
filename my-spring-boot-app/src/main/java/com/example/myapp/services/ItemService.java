package com.example.myapp.services;

import com.example.myapp.models.Item;
import com.example.myapp.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Optional<Item> findByName(String name) {
        return itemRepository.findByName(name);
    }

    public Item save(Item item) {
        try {
            if (item.getId() == null && itemRepository.existsByName(item.getName())) {
                throw new IllegalArgumentException("物品名称 '" + item.getName() + "' 已存在");
            }
            return itemRepository.save(item);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalArgumentException("物品名称 '" + item.getName() + "' 已存在", e);
        }
    }

    public Item update(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("物品不存在，ID: " + id));

        try {
            if (!item.getName().equals(itemDetails.getName())) {
                itemRepository.findByNameForUpdate(itemDetails.getName())
                        .ifPresent(existingItem -> {
                            throw new IllegalArgumentException("物品名称 '" + itemDetails.getName() + "' 已存在");
                        });
            }

            item.setName(itemDetails.getName());
            item.setDescription(itemDetails.getDescription());
            item.setCategory(itemDetails.getCategory());
            item.setQuantity(itemDetails.getQuantity());
            item.setPrice(itemDetails.getPrice());

            return itemRepository.save(item);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("物品名称 '" + itemDetails.getName() + "' 已存在", e);
        }
    }

    public void deleteById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("物品不存在，ID: " + id);
        }
        itemRepository.deleteById(id);
    }

    public List<Item> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return itemRepository.searchByKeyword(keyword.trim());
    }

    public List<Item> findByCategory(String category) {
        return itemRepository.findByCategory(category);
    }

    public List<Item> findLowStockItems(Integer threshold) {
        return itemRepository.findByQuantityLessThan(threshold);
    }

    public List<String> getAllCategories() {
        return itemRepository.findAllCategories();
    }
}