package com.example.myapp.controllers;

import com.example.myapp.models.Item;
import com.example.myapp.models.User;
import com.example.myapp.services.ItemService;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public ProfileController(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    // ─── 个人中心首页 ──────────────────────────────────────────────

    @GetMapping
    public String showProfile(Model model) {
        User user = userService.getOrCreateDefaultUser();
        List<Item> myItems = itemService.findByUserId(user.getId());

        long itemCount = myItems.size();
        BigDecimal totalValue = myItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long lowStockCount = myItems.stream()
                .filter(i -> i.getQuantity() < 5)
                .count();

        model.addAttribute("user", user);
        model.addAttribute("items", myItems);
        model.addAttribute("itemCount", itemCount);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("lowStockCount", lowStockCount);
        return "profile/index";
    }

    // ─── 个人信息编辑 ──────────────────────────────────────────────

    @GetMapping("/edit")
    public String showEditProfile(Model model) {
        User user = userService.getOrCreateDefaultUser();
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute User user,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile/edit";
        }
        try {
            userService.updateUser(user.getId(), user);
            redirectAttributes.addFlashAttribute("success", "个人信息更新成功！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    // ─── 我的物品列表 ──────────────────────────────────────────────

    @GetMapping("/items")
    public String listMyItems(@RequestParam(required = false) String keyword, Model model) {
        User user = userService.getOrCreateDefaultUser();
        List<Item> myItems = itemService.searchByKeywordAndUserId(keyword, user.getId());
        model.addAttribute("user", user);
        model.addAttribute("items", myItems);
        model.addAttribute("keyword", keyword);
        return "profile/items/list";
    }

    // ─── 新增我的物品 ──────────────────────────────────────────────

    @GetMapping("/items/new")
    public String showCreateItemForm(Model model) {
        User user = userService.getOrCreateDefaultUser();
        model.addAttribute("user", user);
        model.addAttribute("item", new Item());
        return "profile/items/form";
    }

    @PostMapping("/items")
    public String createItem(@Valid @ModelAttribute Item item,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User user = userService.getOrCreateDefaultUser();
            model.addAttribute("user", user);
            return "profile/items/form";
        }
        try {
            User user = userService.getOrCreateDefaultUser();
            item.setUserId(user.getId());
            itemService.save(item);
            redirectAttributes.addFlashAttribute("success", "物品创建成功！");
            return "redirect:/profile/items";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/items/new";
        }
    }

    // ─── 编辑我的物品 ──────────────────────────────────────────────

    @GetMapping("/items/{id}/edit")
    public String showEditItemForm(@PathVariable Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        User user = userService.getOrCreateDefaultUser();
        Item item = itemService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("物品不存在，ID: " + id));
        if (!user.getId().equals(item.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "无权限操作该物品");
            return "redirect:/profile/items";
        }
        model.addAttribute("user", user);
        model.addAttribute("item", item);
        return "profile/items/form";
    }

    @PostMapping("/items/{id}")
    public String updateItem(@PathVariable Long id,
                             @Valid @ModelAttribute Item item,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User user = userService.getOrCreateDefaultUser();
            model.addAttribute("user", user);
            return "profile/items/form";
        }
        try {
            User user = userService.getOrCreateDefaultUser();
            Item existing = itemService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("物品不存在，ID: " + id));
            if (!user.getId().equals(existing.getUserId())) {
                redirectAttributes.addFlashAttribute("error", "无权限操作该物品");
                return "redirect:/profile/items";
            }
            item.setUserId(user.getId());
            itemService.update(id, item);
            redirectAttributes.addFlashAttribute("success", "物品更新成功！");
            return "redirect:/profile/items";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/items/" + id + "/edit";
        }
    }

    // ─── 删除我的物品 ──────────────────────────────────────────────

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getOrCreateDefaultUser();
            Item existing = itemService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("物品不存在，ID: " + id));
            if (!user.getId().equals(existing.getUserId())) {
                redirectAttributes.addFlashAttribute("error", "无权限操作该物品");
                return "redirect:/profile/items";
            }
            itemService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "物品删除成功！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile/items";
    }
}
