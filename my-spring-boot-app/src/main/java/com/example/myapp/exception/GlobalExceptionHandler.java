package com.example.myapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                 RedirectAttributes redirectAttributes,
                                                 HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && isInternalUrl(referer, request)) {
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

    private boolean isInternalUrl(String url, HttpServletRequest request) {
        try {
            URI refererUri = new URI(url);
            String refererHost = refererUri.getHost();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            int refererPort = refererUri.getPort() == -1 ? 80 : refererUri.getPort();
            return refererHost != null
                    && refererHost.equalsIgnoreCase(serverName)
                    && refererPort == serverPort;
        } catch (Exception e) {
            return false;
        }
    }
}
