package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link UserService} 单元测试。
 * <p>
 * 使用 Mockito 隔离 {@link UserRepository}，验证 Service 层业务逻辑分支，
 * 包括用户创建的唯一性校验、资料更新、删除、默认用户初始化等。
 */
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
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");
    }

    // ------------------------------------------------------------------
    // createUser
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("createUser 创建用户")
    class CreateUser {

        @Test
        @DisplayName("用户名和邮箱均不重复时，应创建成功")
        void shouldCreateWhenUnique() {
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setEmail("new@example.com");
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.createUser(newUser);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("newuser");
            verify(userRepository, times(1)).save(newUser);
        }

        @Test
        @DisplayName("用户名已存在时，应抛出 IllegalArgumentException")
        void shouldThrowWhenUsernameExists() {
            User duplicate = new User();
            duplicate.setUsername("testuser");
            duplicate.setEmail("other@example.com");
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(duplicate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户名 'testuser' 已存在");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("邮箱已被注册时，应抛出 IllegalArgumentException")
        void shouldThrowWhenEmailExists() {
            User duplicate = new User();
            duplicate.setUsername("otheruser");
            duplicate.setEmail("test@example.com");
            when(userRepository.existsByUsername("otheruser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(duplicate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("邮箱 'test@example.com' 已被注册");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("用户名重复时应优先于邮箱检查抛出异常")
        void shouldCheckUsernameBeforeEmail() {
            User duplicate = new User();
            duplicate.setUsername("testuser");
            duplicate.setEmail("test@example.com");
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(duplicate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户名");

            // 用户名重复时不应再检查邮箱
            verify(userRepository, never()).existsByEmail(anyString());
        }
    }

    // ------------------------------------------------------------------
    // getUserById
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("getUserById 按 ID 查询用户")
    class GetUserById {

        @Test
        @DisplayName("用户存在时，应返回 Optional 含值")
        void shouldReturnUserWhenExists() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

            Optional<User> result = userService.getUserById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("用户不存在时，应返回空 Optional")
        void shouldReturnEmptyWhenNotExists() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<User> result = userService.getUserById(999L);

            assertThat(result).isEmpty();
        }
    }

    // ------------------------------------------------------------------
    // getAllUsers
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getAllUsers 仓库返回多条记录时应原样返回")
    void getAllUsers_shouldReturnAll() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(sampleUser, new User()));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
    }

    // ------------------------------------------------------------------
    // updateProfile
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("updateProfile 更新用户资料")
    class UpdateProfile {

        @Test
        @DisplayName("用户不存在时，应抛出 IllegalArgumentException")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateProfile(999L, new User()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户不存在，ID: 999");
        }

        @Test
        @DisplayName("用户名和邮箱均未变时，应直接更新并保存")
        void shouldUpdateWhenUnchanged() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User details = new User();
            details.setUsername("testuser");
            details.setEmail("test@example.com");
            details.setPhone("13800000000");
            details.setBio("新简介");
            details.setLocation("杭州");
            details.setAvatarUrl("http://example.com/avatar.png");

            User result = userService.updateProfile(1L, details);

            assertThat(result.getPhone()).isEqualTo("13800000000");
            assertThat(result.getBio()).isEqualTo("新简介");
            assertThat(result.getLocation()).isEqualTo("杭州");
            assertThat(result.getAvatarUrl()).isEqualTo("http://example.com/avatar.png");
            // 用户名未变，不应调用 existsByUsername
            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("新用户名已被其他用户占用时，应抛出 IllegalArgumentException")
        void shouldThrowWhenNewUsernameTaken() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByUsername("newuser")).thenReturn(true);

            User details = new User();
            details.setUsername("newuser");
            details.setEmail("test@example.com");

            assertThatThrownBy(() -> userService.updateProfile(1L, details))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户名 'newuser' 已存在");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("新邮箱已被其他用户注册时，应抛出 IllegalArgumentException")
        void shouldThrowWhenNewEmailTaken() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

            User details = new User();
            details.setUsername("newuser");
            details.setEmail("new@example.com");

            assertThatThrownBy(() -> userService.updateProfile(1L, details))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("邮箱 'new@example.com' 已被注册");
        }

        @Test
        @DisplayName("用户名和邮箱均变更且均不重复时，应更新全部字段")
        void shouldUpdateAllFieldsWhenBothChanged() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User details = new User();
            details.setUsername("newuser");
            details.setEmail("new@example.com");
            details.setPhone("13900000000");
            details.setBio("已更新");
            details.setLocation("上海");
            details.setAvatarUrl("http://example.com/new.png");

            User result = userService.updateProfile(1L, details);

            assertThat(result.getUsername()).isEqualTo("newuser");
            assertThat(result.getEmail()).isEqualTo("new@example.com");
            assertThat(result.getPhone()).isEqualTo("13900000000");
            assertThat(result.getBio()).isEqualTo("已更新");
            assertThat(result.getLocation()).isEqualTo("上海");
            assertThat(result.getAvatarUrl()).isEqualTo("http://example.com/new.png");
        }
    }

    // ------------------------------------------------------------------
    // deleteUser
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("deleteUser 删除用户")
    class DeleteUser {

        @Test
        @DisplayName("用户存在时，应执行删除")
        void shouldDeleteWhenExists() {
            when(userRepository.existsById(1L)).thenReturn(true);

            userService.deleteUser(1L);

            verify(userRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("用户不存在时，应抛出 IllegalArgumentException 且不执行删除")
        void shouldThrowWhenNotExists() {
            when(userRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户不存在，ID: 999");

            verify(userRepository, never()).deleteById(anyLong());
        }
    }

    // ------------------------------------------------------------------
    // getOrCreateDefaultUser
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("getOrCreateDefaultUser 获取或初始化默认用户")
    class GetOrCreateDefaultUser {

        @Test
        @DisplayName("仓库已有用户时，应返回第一个用户")
        void shouldReturnExistingUserWhenNotEmpty() {
            when(userRepository.findAll()).thenReturn(Collections.singletonList(sampleUser));

            User result = userService.getOrCreateDefaultUser();

            assertThat(result).isSameAs(sampleUser);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("仓库无用户时，应创建并返回默认用户")
        void shouldCreateDefaultWhenEmpty() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            User result = userService.getOrCreateDefaultUser();

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("admin");
            assertThat(result.getEmail()).isEqualTo("admin@example.com");
            assertThat(result.getBio()).isEqualTo("这是默认用户，欢迎编辑个人信息！");
            assertThat(result.getLocation()).isEqualTo("中国");
            verify(userRepository, times(1)).save(any(User.class));
        }
    }
}
