package com.example.myapp.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ErrorViewController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        Object errorCode = request.getAttribute("errorCode");
        if (errorCode == null) {
            errorCode = request.getAttribute("javax.servlet.error.status_code");
        }
        Object errorMessage = request.getAttribute("error");
        if (errorMessage == null) {
            errorMessage = request.getAttribute("javax.servlet.error.message");
        }
        if (errorMessage == null || "".equals(errorMessage)) {
            errorMessage = "发生了未知错误";
        }

        model.addAttribute("errorCode", errorCode != null ? errorCode : 500);
        model.addAttribute("error", errorMessage);
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return "error";
    }
}
