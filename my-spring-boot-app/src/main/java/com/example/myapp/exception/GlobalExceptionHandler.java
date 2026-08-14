package com.example.myapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException ex,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorCode", ex.getCode());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex,
                                                HttpServletRequest request,
                                                RedirectAttributes redirectAttributes) {
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ErrorCode.NOT_FOUND, ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.NOT_FOUND.value());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public Object handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex,
                                                      HttpServletRequest request,
                                                      RedirectAttributes redirectAttributes) {
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(ErrorCode.CONFLICT, ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.CONFLICT.value());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && isInternalUrl(referer, request)) {
            return "redirect:" + referer;
        }
        return "redirect:/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException ex,
                                               HttpServletRequest request,
                                               RedirectAttributes redirectAttributes) {
        if (isAjaxRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && isInternalUrl(referer, request)) {
            return "redirect:" + referer;
        }
        return "redirect:/items";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                        HttpServletRequest request,
                                                        RedirectAttributes redirectAttributes) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        if (isAjaxRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, message));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/error";
    }

    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException ex,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        if (isAjaxRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, message));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/error";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException ex,
                                                                HttpServletRequest request,
                                                                RedirectAttributes redirectAttributes) {
        String message = "缺少必要参数: " + ex.getParameterName();
        if (isAjaxRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorCode.BAD_REQUEST, message));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/error";
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                                                      HttpServletRequest request,
                                                      RedirectAttributes redirectAttributes) {
        String message = "请求体格式错误";
        if (isAjaxRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorCode.BAD_REQUEST, message));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex,
                                                               HttpServletRequest request,
                                                               RedirectAttributes redirectAttributes) {
        String message = "请求方法不支持: " + ex.getMethod();
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED, message));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.METHOD_NOT_ALLOWED.value());
        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                HttpServletRequest request,
                                                RedirectAttributes redirectAttributes) {
        String message = "页面不存在: " + ex.getRequestURL();
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ErrorCode.NOT_FOUND, message));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.NOT_FOUND.value());
        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        String message = ex.getMessage() != null ? ex.getMessage() : "未知错误";
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, message));
        }
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        redirectAttributes.addFlashAttribute("error", "系统错误: " + message);
        return "redirect:/error";
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String xRequestedWith = request.getHeader("X-Requested-With");
        return (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith);
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
