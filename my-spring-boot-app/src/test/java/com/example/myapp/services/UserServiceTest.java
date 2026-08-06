package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link UserService} 单元测试。
 *
 * <p>覆盖全部公开方法的正常路径、边界条件与异常路径，遵循 FIRST 原则：
 * <ul>
 *   <li>Fast：纯 Mockito Mock，不启动 Spring 上下文</li>
 *   <li>Independent：每个用例独立初始化 Mock</li>
 *   <li>Repeatable：无外部状态依赖</li>
 *   <li>Self-validating：AssertJ 断言自带校验信息</li>
 *   <li>Timely：与被测代码同步编写</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 业务逻辑测试")
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
        sampleUser.setAvatarUrl("https://example.com/a.png");
    }

    // ==================== createUser ====================

    @Test
    @DisplayName("createUser：用户名与邮箱均未占用时正常创建并保存")
    void createUser_whenUsernameAndEmailAreFree_shouldSave() {
        // given
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        // when
        User created = userService.createUser(sampleUser);

        // then
        assertThat(created).isSameAs(sampleUser);
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("createUser：用户名已存在时抛出 IllegalArgumentException 且不保存")
    void createUser_whenUsernameExists_shouldThrowAndNotSave() {
        // given
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(sampleUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("alice")
                .hasMessageContaining("已存在");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser：邮箱已被注册时抛出 IllegalArgumentException 且不保存")
    void createUser_whenEmailExists_shouldThrowAndNotSave() {
        // given
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(sampleUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("alice@example.com")
                .hasMessageContaining("已被注册");

        verify(userRepository, never()).save(any());
    }

    // ==================== getUserById ====================

    @Test
    @DisplayName("getUserById：用户存在时返回 Optional.of(user)")
    void getUserById_whenUserExists_shouldReturnUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        // when
        Optional<User> result = userService.getUserById(1L);

        // then
        assertThat(result).isPresent().containsSame(sampleUser);
    }

    @Test
    @DisplayName("getUserById：用户不存在时返回 Optional.empty()")
    void getUserById_whenUserNotExists_shouldReturnEmpty() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.getUserById(999L);

        // then
        assertThat(result).isEmpty();
    }

    // ==================== getAllUsers ====================

    @Test
    @DisplayName("getAllUsers：返回仓储中的全部用户列表")
    void getAllUsers_shouldReturnAllUsers() {
        // given
        User second = new User();
        second.setId(2L);
        second.setUsername("bob");
        second.setEmail("bob@example.com");
        when(userRepository.findAll()).thenReturn(List.of(sampleUser, second));

        // when
        List<User> users = userService.getAllUsers();

        // then
        assertThat(users).hasSize(2)
                .extracting(User::getUsername)
                .containsExactly("alice", "bob");
    }

    // ==================== updateProfile ====================

    @Test
    @DisplayName("updateProfile：用户名与邮箱均未变更时正常更新全部可编辑字段")
    void updateProfile_whenUsernameAndEmailUnchanged_shouldUpdateFields() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User details = new User();
        details.setUsername("alice");
        details.setEmail("alice@example.com");
        details.setPhone("13900000000");
        details.setBio("更新后的简介");
        details.setLocation("上海");
        details.setAvatarUrl("https://example.com/new.png");

        // when
        User updated = userService.updateProfile(1L, details);

        // then
        assertThat(updated.getPhone()).isEqualTo("13900000000");
        assertThat(updated.getBio()).isEqualTo("更新后的简介");
        assertThat(updated.getLocation()).isEqualTo("上海");
        assertThat(updated.getAvatarUrl()).isEqualTo("https://example.com/new.png");
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("updateProfile：新用户名与原用户名不同且已被占用时抛出异常")
    void updateProfile_whenNewUsernameTakenByOther_shouldThrow() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByUsername("bob")).thenReturn(true);

        User details = new User();
        details.setUsername("bob");
        details.setEmail("alice@example.com");

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bob")
                .hasMessageContaining("已存在");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile：新邮箱与原邮箱不同且已被占用时抛出异常")
    void updateProfile_whenNewEmailTakenByOther_shouldThrow() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        User details = new User();
        details.setUsername("alice");
        details.setEmail("bob@example.com");

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bob@example.com")
                .hasMessageContaining("已被注册");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile：用户不存在时抛出异常")
    void updateProfile_whenUserNotExists_shouldThrow() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User details = new User();
        details.setUsername("alice");
        details.setEmail("alice@example.com");

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(999L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在")
                .hasMessageContaining("999");
    }

    // ==================== deleteUser ====================

    @Test
    @DisplayName("deleteUser：用户存在时正常删除")
    void deleteUser_whenUserExists_shouldDelete() {
        // given
        when(userRepository.existsById(1L)).thenReturn(true);

        // when
        userService.deleteUser(1L);

        // then
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser：用户不存在时抛出异常且不执行删除")
    void deleteUser_whenUserNotExists_shouldThrowAndNotDelete() {
        // given
        when(userRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在")
                .hasMessageContaining("999");

        verify(userRepository, never()).deleteById(any());
    }

    // ==================== getOrCreateDefaultUser ====================

    @Test
    @DisplayName("getOrCreateDefaultUser：已有用户时返回首个用户且不创建新用户")
    void getOrCreateDefaultUser_whenUsersExist_shouldReturnFirstAndNotCreate() {
        // given
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        // when
        User result = userService.getOrCreateDefaultUser();

        // then
        assertThat(result).isSameAs(sampleUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getOrCreateDefaultUser：无任何用户时创建 admin 默认用户并保存")
    void getOrCreateDefaultUser_whenNoUsersExist_shouldCreateDefault() {
        // given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = userService.getOrCreateDefaultUser();

        // then
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getEmail()).isEqualTo("admin@example.com");
        assertThat(result.getBio()).isEqualTo("这是默认用户，欢迎编辑个人信息！");
        assertThat(result.getLocation()).isEqualTo("中国");
        verify(userRepository, times(1)).save(any(User.class));
    }
}
