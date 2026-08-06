package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 * 遵循 FIRST 原则 (Fast, Independent, Repeatable, Self-Validating, Timely)
 * 使用 Mockito 隔离 Repository 依赖，不依赖真实数据库
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
        sampleUser.setPassword("password123");
        sampleUser.setLocation("中国");
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());
    }

    // ======================== findByUsername ========================

    @Nested
    @DisplayName("findByUsername 方法")
    class FindByUsernameTests {

        @Test
        @DisplayName("根据存在的用户名查找用户应返回用户")
        void shouldReturnUserWhenUsernameExists() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

            Optional<User> result = userService.findByUsername("testuser");

            assertTrue(result.isPresent());
            assertEquals("testuser", result.get().getUsername());
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("根据不存在的用户名查找用户应返回空 Optional")
        void shouldReturnEmptyWhenUsernameNotFound() {
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            Optional<User> result = userService.findByUsername("nonexistent");

            assertFalse(result.isPresent());
            verify(userRepository).findByUsername("nonexistent");
        }

        @Test
        @DisplayName("传入 null 用户名应委托 Repository 处理")
        void shouldDelegateNullUsernameToRepository() {
            when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

            Optional<User> result = userService.findByUsername(null);

            assertFalse(result.isPresent());
            verify(userRepository).findByUsername(null);
        }
    }

    // ======================== findByEmail ========================

    @Nested
    @DisplayName("findByEmail 方法")
    class FindByEmailTests {

        @Test
        @DisplayName("根据存在的邮箱查找用户应返回用户")
        void shouldReturnUserWhenEmailExists() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

            Optional<User> result = userService.findByEmail("test@example.com");

            assertTrue(result.isPresent());
            assertEquals("test@example.com", result.get().getEmail());
            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("根据不存在的邮箱查找用户应返回空 Optional")
        void shouldReturnEmptyWhenEmailNotFound() {
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            Optional<User> result = userService.findByEmail("unknown@example.com");

            assertFalse(result.isPresent());
            verify(userRepository).findByEmail("unknown@example.com");
        }
    }

    // ======================== register ========================

    @Nested
    @DisplayName("register 方法")
    class RegisterTests {

        @Test
        @DisplayName("注册新用户成功应返回保存后的用户")
        void shouldRegisterNewUserSuccessfully() {
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setEmail("new@example.com");
            newUser.setPassword("pass123");

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(2L);
                return u;
            });

            User result = userService.register(newUser);

            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals("newuser", result.getUsername());
            assertEquals("中国", result.getLocation());
            verify(userRepository).existsByUsername("newuser");
            verify(userRepository).existsByEmail("new@example.com");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("用户名已存在时注册应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenUsernameExists() {
            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setEmail("another@example.com");

            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register(newUser)
            );

            assertTrue(exception.getMessage().contains("用户名"));
            assertTrue(exception.getMessage().contains("已存在"));
            verify(userRepository).existsByUsername("testuser");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("邮箱已存在时注册应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenEmailExists() {
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setEmail("test@example.com");

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register(newUser)
            );

            assertTrue(exception.getMessage().contains("邮箱"));
            assertTrue(exception.getMessage().contains("已存在"));
            verify(userRepository).existsByUsername("newuser");
            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("用户名和邮箱格式错误时注册应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenUsernameAndEmailFormatInvalid() {
            User newUser = new User();
            newUser.setUsername("ab");
            newUser.setEmail("invalid-email");

            when(userRepository.existsByUsername("ab")).thenReturn(false);
            when(userRepository.existsByEmail("invalid-email")).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register(newUser)
            );

            assertTrue(exception.getMessage().contains("用户名") || exception.getMessage().contains("邮箱"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("注册用户时默认位置应设置为 '中国'")
        void shouldSetDefaultLocationWhenRegistering() {
            User newUser = new User();
            newUser.setUsername("chinaUser");
            newUser.setEmail("china@example.com");
            newUser.setPassword("pass456");

            when(userRepository.existsByUsername("chinaUser")).thenReturn(false);
            when(userRepository.existsByEmail("china@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.register(newUser);

            assertEquals("中国", result.getLocation());
            verify(userRepository).save(any(User.class));
        }
    }

    // ======================== update ========================

    @Nested
    @DisplayName("update 方法")
    class UpdateTests {

        @Test
        @DisplayName("更新存在的用户信息应返回更新后的用户")
        void shouldUpdateExistingUser() {
            User updatedDetails = new User();
            updatedDetails.setUsername("updatedUser");
            updatedDetails.setEmail("updated@example.com");
            updatedDetails.setLocation("北京");

            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByUsername("updatedUser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.update(1L, updatedDetails);

            assertNotNull(result);
            assertEquals("updatedUser", result.getUsername());
            assertEquals("updated@example.com", result.getEmail());
            assertEquals("北京", result.getLocation());
            verify(userRepository).findById(1L);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("更新不存在的用户应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenUserNotFound() {
            User updatedDetails = new User();
            updatedDetails.setUsername("updatedUser");

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.update(999L, updatedDetails)
            );

            assertTrue(exception.getMessage().contains("用户不存在"));
            verify(userRepository).findById(999L);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("更新时用户名冲突应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenUpdatedUsernameConflicts() {
            User updatedDetails = new User();
            updatedDetails.setUsername("conflictUser");
            updatedDetails.setEmail("original@example.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByUsername("conflictUser")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.update(1L, updatedDetails)
            );

            assertTrue(exception.getMessage().contains("用户名"));
            assertTrue(exception.getMessage().contains("已存在"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("更新时邮箱冲突应抛出 IllegalArgumentException")
        void shouldThrowExceptionWhenUpdatedEmailConflicts() {
            User updatedDetails = new User();
            updatedDetails.setUsername("testuser");
            updatedDetails.setEmail("conflict@example.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByUsername("testuser")).thenReturn(false);
            when(userRepository.existsByEmail("conflict@example.com")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.update(1L, updatedDetails)
            );

            assertTrue(exception.getMessage().contains("邮箱"));
            assertTrue(exception.getMessage().contains("已存在"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("更新时保持原用户名不检查冲突")
        void shouldNotCheckUsernameConflictWhenKeepingSameUsername() {
            User updatedDetails = new User();
            updatedDetails.setUsername("testuser");
            updatedDetails.setEmail("newemail@example.com");
            updatedDetails.setLocation("上海");

            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.update(1L, updatedDetails);

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("更新时保留原位置如果未提供新位置")
        void shouldKeepOriginalLocationWhenNotProvided() {
            sampleUser.setLocation("广州");
            User updatedDetails = new User();
            updatedDetails.setUsername("testuser");
            updatedDetails.setEmail("test@example.com");
            updatedDetails.setLocation(null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.update(1L, updatedDetails);

            assertEquals("广州", result.getLocation());
        }
    }

    // ======================== findOrCreateDefaultUser ========================

    @Nested
    @DisplayName("findOrCreateDefaultUser 方法")
    class FindOrCreateDefaultUserTests {

        @Test
        @DisplayName("已存在默认用户时应返回该用户不创建")
        void shouldReturnExistingDefaultUser() {
            when(userRepository.findByUsername("default")).thenReturn(Optional.of(sampleUser));

            User result = userService.findOrCreateDefaultUser();

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            verify(userRepository).findByUsername("default");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("默认用户不存在时应创建并返回")
        void shouldCreateDefaultUserWhenNotFound() {
            when(userRepository.findByUsername("default")).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(100L);
                return u;
            });

            User result = userService.findOrCreateDefaultUser();

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals("default", result.getUsername());
            assertEquals("default@example.com", result.getEmail());
            assertEquals("中国", result.getLocation());
            verify(userRepository).findByUsername("default");
            verify(userRepository).save(any(User.class));
        }
    }
}