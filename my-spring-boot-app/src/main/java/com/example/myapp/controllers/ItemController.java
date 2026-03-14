package com.example.myapp.controllers;

import com.example.myapp.models.Item;
import com.example.myapp.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

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
    public String createItem(@ModelAttribute Item item) {
        itemService.save(item);
        return "redirect:/items";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item id: " + id));
        model.addAttribute("item", item);
        return "items/form";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable Long id, @ModelAttribute Item item) {
        item.setId(id);
        itemService.save(item);
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        itemService.deleteById(id);
        return "redirect:/items";
    }


    @GetMapping("/{id}")
    public String viewItem(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        model.addAttribute("item", item);
        return "items/view";
    }
}