package com.example.myapp.controllers;

import com.example.myapp.models.User;
import com.example.myapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 个人中心主页 - 展示当前用户信息
     */
    @GetMapping
    public String viewProfile(Model model) {
        User currentUser = userService.getOrCreateDefaultUser();
        model.addAttribute("user", currentUser);
        return "profile/view";
    }

    /**
     * 进入编辑个人信息页面
     */
    @GetMapping("/edit")
    public String showEditForm(Model model) {
        User currentUser = userService.getOrCreateDefaultUser();
        model.addAttribute("user", currentUser);
        return "profile/edit";
    }

    /**
     * 提交个人信息修改
     */
    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute User user,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile/edit";
        }
        try {
            userService.updateProfile(user.getId(), user);
            redirectAttributes.addFlashAttribute("success", "个人信息更新成功！");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit";
        }
    }
}
