package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link UserService} 单元测试。
 * <p>
 * 覆盖用户创建、资料更新、删除及默认用户获取等核心业务路径，
 * 使用 Mockito 隔离 {@link UserRepository} 依赖，遵循 FIRST 原则。
 * </p>
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("alice");
        sampleUser.setEmail("alice@example.com");
        sampleUser.setPhone("13800000000");
        sampleUser.setBio("测试用户");
        sampleUser.setLocation("杭州");
        sampleUser.setAvatarUrl("/avatar/alice.png");
    }

    // ==================== createUser ====================

    @Test
    @DisplayName("createUser: 用户名和邮箱均未占用时创建成功")
    void should_createUser_when_usernameAndEmailAvailable() {
        // given
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User newUser = new User();
        newUser.setUsername("bob");
        newUser.setEmail("bob@example.com");

        // when
        User result = userService.createUser(newUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("bob");
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("createUser: 用户名已存在时抛出 IllegalArgumentException")
    void should_throw_when_createUserWithDuplicateUsername() {
        // given
        when(userRepository.existsByUsername("alice")).thenReturn(true);
        User duplicate = new User();
        duplicate.setUsername("alice");
        duplicate.setEmail("alice@example.com");

        // when & then
        assertThatThrownBy(() -> userService.createUser(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户名")
                .hasMessageContaining("已存在");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: 邮箱已被注册时抛出 IllegalArgumentException")
    void should_throw_when_createUserWithDuplicateEmail() {
        // given
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);
        User duplicate = new User();
        duplicate.setUsername("bob");
        duplicate.setEmail("alice@example.com");

        // when & then
        assertThatThrownBy(() -> userService.createUser(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("邮箱")
                .hasMessageContaining("已被注册");
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== getUserById ====================

    @Test
    @DisplayName("getUserById: 用户存在时返回 Optional")
    void should_returnUser_when_getByIdAndExists() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        // when
        Optional<User> result = userService.getUserById(1L);

        // then
        assertThat(result).isPresent().contains(sampleUser);
    }

    @Test
    @DisplayName("getUserById: 用户不存在时返回 empty Optional")
    void should_returnEmpty_when_getByIdAndNotExists() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.getUserById(99L);

        // then
        assertThat(result).isEmpty();
    }

    // ==================== getAllUsers ====================

    @Test
    @DisplayName("getAllUsers: 返回全部用户列表")
    void should_returnAllUsers_when_getAllUsers() {
        // given
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        // when
        List<User> result = userService.getAllUsers();

        // then
        assertThat(result).isNotEmpty().hasSize(1).contains(sampleUser);
    }

    @Test
    @DisplayName("getAllUsers: 无数据时返回空列表")
    void should_returnEmpty_when_noUsers() {
        // given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<User> result = userService.getAllUsers();

        // then
        assertThat(result).isEmpty();
    }

    // ==================== updateProfile ====================

    @Test
    @DisplayName("updateProfile: 用户不存在时抛出 IllegalArgumentException")
    void should_throw_when_updateNonExistentUser() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(99L, sampleUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不存在");
    }

    @Test
    @DisplayName("updateProfile: 用户名与邮箱均未变更时直接更新")
    void should_update_when_usernameAndEmailUnchanged() {
        // given
        User details = new User();
        details.setUsername("alice");
        details.setEmail("alice@example.com");
        details.setPhone("13900000000");
        details.setBio("更新简介");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = userService.updateProfile(1L, details);

        // then
        assertThat(result.getPhone()).isEqualTo("13900000000");
        assertThat(result.getBio()).isEqualTo("更新简介");
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    @DisplayName("updateProfile: 用户名变更为未占用名称时更新成功")
    void should_update_when_usernameChangedAndAvailable() {
        // given
        User details = new User();
        details.setUsername("alice_new");
        details.setEmail("alice@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByUsername("alice_new")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = userService.updateProfile(1L, details);

        // then
        assertThat(result.getUsername()).isEqualTo("alice_new");
        verify(userRepository).existsByUsername("alice_new");
    }

    @Test
    @DisplayName("updateProfile: 用户名变更为已占用名称时抛出 IllegalArgumentException")
    void should_throw_when_updateToOccupiedUsername() {
        // given
        User details = new User();
        details.setUsername("bob");
        details.setEmail("alice@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByUsername("bob")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户名")
                .hasMessageContaining("已存在");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateProfile: 邮箱变更为已注册邮箱时抛出 IllegalArgumentException")
    void should_throw_when_updateToRegisteredEmail() {
        // given
        User details = new User();
        details.setUsername("alice");
        details.setEmail("bob@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("邮箱")
                .hasMessageContaining("已被注册");
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== deleteUser ====================

    @Test
    @DisplayName("deleteUser: 用户存在时删除成功")
    void should_delete_when_userExists() {
        // given
        when(userRepository.existsById(1L)).thenReturn(true);

        // when
        userService.deleteUser(1L);

        // then
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: 用户不存在时抛出 IllegalArgumentException")
    void should_throw_when_deleteNonExistentUser() {
        // given
        when(userRepository.existsById(99L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不存在");
        verify(userRepository, never()).deleteById(anyLong());
    }

    // ==================== getOrCreateDefaultUser ====================

    @Test
    @DisplayName("getOrCreateDefaultUser: 已有用户时返回首个用户")
    void should_returnFirstUser_when_usersExist() {
        // given
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        // when
        User result = userService.getOrCreateDefaultUser();

        // then
        assertThat(result).isEqualTo(sampleUser);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getOrCreateDefaultUser: 无用户时创建默认用户并保存")
    void should_createDefaultUser_when_noUsersExist() {
        // given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = userService.getOrCreateDefaultUser();

        // then
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getEmail()).isEqualTo("admin@example.com");
        assertThat(result.getBio()).isEqualTo("这是默认用户，欢迎编辑个人信息！");
        assertThat(result.getLocation()).isEqualTo("中国");
        verify(userRepository).save(any(User.class));
    }
}
