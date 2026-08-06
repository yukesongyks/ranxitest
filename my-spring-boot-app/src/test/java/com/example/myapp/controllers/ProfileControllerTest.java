package com.example.myapp.controllers;

import com.example.myapp.models.User;
import com.example.myapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ProfileController} 单元测试。
 * <p>
 * 覆盖个人中心主页、编辑页、提交更新等入口路径，
 * 使用 Mockito 隔离 {@link UserService} 及 Spring MVC 组件（{@link Model}、
 * {@link BindingResult}、{@link RedirectAttributes}），遵循 FIRST 原则。
 * </p>
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileController 单元测试")
class ProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProfileController profileController;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("alice");
        sampleUser.setEmail("alice@example.com");
    }

    // ==================== viewProfile ====================

    @Test
    @DisplayName("viewProfile: 将默认用户放入 Model 并返回 profile/view 视图")
    void should_addUserToModelAndReturnView_when_viewProfile() {
        // given
        when(userService.getOrCreateDefaultUser()).thenReturn(sampleUser);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        // when
        String view = profileController.viewProfile(model);

        // then
        assertThat(view).isEqualTo("profile/view");
        verify(userService).getOrCreateDefaultUser();
        verify(model).addAttribute(eq("user"), eq(sampleUser));
    }

    // ==================== showEditForm ====================

    @Test
    @DisplayName("showEditForm: 将默认用户放入 Model 并返回 profile/edit 视图")
    void should_addUserToModelAndReturnEditView_when_showEditForm() {
        // given
        when(userService.getOrCreateDefaultUser()).thenReturn(sampleUser);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        // when
        String view = profileController.showEditForm(model);

        // then
        assertThat(view).isEqualTo("profile/edit");
        verify(model).addAttribute(eq("user"), eq(sampleUser));
    }

    // ==================== updateProfile ====================

    @Test
    @DisplayName("updateProfile: 校验失败时返回编辑页且不调用 Service")
    void should_returnEditView_when_bindingErrors() {
        // given
        when(bindingResult.hasErrors()).thenReturn(true);

        // when
        String view = profileController.updateProfile(sampleUser, bindingResult, redirectAttributes);

        // then
        assertThat(view).isEqualTo("profile/edit");
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    @DisplayName("updateProfile: 更新成功时设置成功 flash 并重定向到 /profile")
    void should_redirectToProfile_when_updateSucceeds() {
        // given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateProfile(eq(1L), any(User.class))).thenReturn(sampleUser);

        // when
        String view = profileController.updateProfile(sampleUser, bindingResult, redirectAttributes);

        // then
        assertThat(view).isEqualTo("redirect:/profile");
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("updateProfile: Service 抛出 IllegalArgumentException 时设置错误 flash 并重定向回编辑页")
    void should_redirectToEdit_when_updateThrowsIllegalArgument() {
        // given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateProfile(eq(1L), any(User.class)))
                .thenThrow(new IllegalArgumentException("用户名 'bob' 已存在"));

        // when
        String view = profileController.updateProfile(sampleUser, bindingResult, redirectAttributes);

        // then
        assertThat(view).isEqualTo("redirect:/profile/edit");
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    @DisplayName("updateProfile: 用户 id 为空时仍按入参调用 Service")
    void should_callServiceWithUserId_when_idNull() {
        // given
        User userWithoutId = new User();
        userWithoutId.setUsername("alice");
        userWithoutId.setEmail("alice@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateProfile(eq(null), any(User.class))).thenReturn(sampleUser);

        // when
        String view = profileController.updateProfile(userWithoutId, bindingResult, redirectAttributes);

        // then
        assertThat(view).isEqualTo("redirect:/profile");
        verify(userService).updateProfile(eq(null), any(User.class));
    }
}
