package com.example.myapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, 
                                                 RedirectAttributes redirectAttributes,
                                                 HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        String referer = request.getHeader("Referer");
        // 验证Referer是否为内部URL，防止开放重定向漏洞
        if (referer != null && isInternalUrl(referer)) {
            return "redirect:" + referer;
        }
        return "redirect:/items";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "未知错误";
        model.addAttribute("error", "系统错误: " + errorMessage);
        return "error";
    }
}
