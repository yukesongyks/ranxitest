package com.example.myapp.controllers;

import com.example.myapp.docgen.DocgenExportProperties;
import com.example.myapp.models.Item;
import com.example.myapp.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final DocgenExportProperties docgenExportProperties;

    @Autowired
    public ItemController(ItemService itemService, DocgenExportProperties docgenExportProperties) {
        this.itemService = itemService;
        this.docgenExportProperties = docgenExportProperties;
    }

    /**
     * TXT 导出功能开关状态（供页面控制「导出TXT」入口显隐）。
     *
     * @return 导出功能是否开启
     */
    @ModelAttribute("docgenExportEnabled")
    public boolean docgenExportEnabled() {
        return docgenExportProperties.isEnabled();
    }

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "items/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new Item());
        return "items/form";
    }

    @PostMapping
    public String createItem(@Valid @ModelAttribute Item item, 
                           BindingResult result, 
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "items/form";
        }
        try {
            itemService.save(item);
            redirectAttributes.addFlashAttribute("success", "物品创建成功！");
            return "redirect:/items";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/items/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item id: " + id));
        model.addAttribute("item", item);
        return "items/form";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable Long id, 
                           @Valid @ModelAttribute Item item, 
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "items/form";
        }
        try {
            itemService.update(id, item);
            redirectAttributes.addFlashAttribute("success", "物品更新成功！");
            return "redirect:/items";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/items/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            itemService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "物品删除成功！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/items";
    }

    @GetMapping("/search")
    public String searchItems(@RequestParam(required = false) String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        List<Item> allItems = itemService.searchByKeyword(keyword);
        model.addAttribute("items", allItems);
        model.addAttribute("keyword", keyword);
        return "items/list";
    }

    @GetMapping("/category/{category}")
    public String getItemsByCategory(@PathVariable String category,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        List<Item> allItems = itemService.findByCategory(category);
        model.addAttribute("items", allItems);
        model.addAttribute("category", category);
        return "items/list";
    }


    @GetMapping("/{id}")
    public String viewItem(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Item item = itemService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("物品不存在，ID: " + id));
            model.addAttribute("item", item);
            return "items/view";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/items";
        }
    }
}