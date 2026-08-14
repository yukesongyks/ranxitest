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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User createDefaultUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setBio("Test bio");
        user.setLocation("Beijing");
        user.setAvatarUrl("http://example.com/avatar.png");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    // ==================== createUser 测试 ====================

    @Nested
    @DisplayName("createUser 方法")
    class CreateUser {

        @Test
        @DisplayName("正常创建用户 - 用户名和邮箱均未重复")
        void should_createUser_when_usernameAndEmailNotExist() {
            // Arrange
            User user = createDefaultUser();
            when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Act
            User result = userService.createUser(user);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("用户名已存在时抛出异常")
        void should_throwException_when_usernameAlreadyExists() {
            // Arrange
            User user = createDefaultUser();
            when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户名 '" + user.getUsername() + "' 已存在");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("邮箱已注册时抛出异常")
        void should_throwException_when_emailAlreadyExists() {
            // Arrange
            User user = createDefaultUser();
            when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("邮箱 '" + user.getEmail() + "' 已被注册");
            verify(userRepository, never()).save(any());
        }
    }

    // ==================== getUserById 测试 ====================

    @Nested
    @DisplayName("getUserById 方法")
    class GetUserById {

        @Test
        @DisplayName("用户存在时返回用户")
        void should_returnUser_when_userExists() {
            // Arrange
            User user = createDefaultUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // Act
            Optional<User> result = userService.getUserById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("用户不存在时返回空")
        void should_returnEmpty_when_userNotExists() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userService.getUserById(999L);

            // Assert
            assertThat(result).isNotPresent();
        }
    }

    // ==================== getAllUsers 测试 ====================

    @Nested
    @DisplayName("getAllUsers 方法")
    class GetAllUsers {

        @Test
        @DisplayName("返回所有用户列表")
        void should_returnAllUsers() {
            // Arrange
            User user1 = createDefaultUser();
            User user2 = createDefaultUser();
            user2.setId(2L);
            user2.setUsername("user2");
            when(userRepository.findAll()).thenReturn(List.of(user1, user2));

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(User::getUsername).contains("testuser", "user2");
        }

        @Test
        @DisplayName("无用户时返回空列表")
        void should_returnEmptyList_when_noUsers() {
            // Arrange
            when(userRepository.findAll()).thenReturn(List.of());

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // ==================== updateProfile 测试 ====================

    @Nested
    @DisplayName("updateProfile 方法")
    class UpdateProfile {

        @Test
        @DisplayName("正常更新用户资料 - 用户名和邮箱均未变更")
        void should_updateProfile_when_usernameAndEmailUnchanged() {
            // Arrange
            User existingUser = createDefaultUser();
            User profileDetails = createDefaultUser();
            profileDetails.setPhone("13900139000");
            profileDetails.setBio("Updated bio");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            // Act
            User result = userService.updateProfile(1L, profileDetails);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPhone()).isEqualTo("13900139000");
            assertThat(result.getBio()).isEqualTo("Updated bio");
            verify(userRepository, times(1)).save(existingUser);
        }

        @Test
        @DisplayName("更新时用户名已存在且非本人则抛出异常")
        void should_throwException_when_usernameAlreadyUsedByOther() {
            // Arrange
            User existingUser = createDefaultUser();
            User profileDetails = createDefaultUser();
            profileDetails.setUsername("otheruser");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByUsername("otheruser")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.updateProfile(1L, profileDetails))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户名 'otheruser' 已存在");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("更新时邮箱已存在且非本人则抛出异常")
        void should_throwException_when_emailAlreadyUsedByOther() {
            // Arrange
            User existingUser = createDefaultUser();
            User profileDetails = createDefaultUser();
            profileDetails.setEmail("other@example.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByUsername(profileDetails.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail("other@example.com")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.updateProfile(1L, profileDetails))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("邮箱 'other@example.com' 已被注册");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("用户不存在时抛出异常")
        void should_throwException_when_userNotFound() {
            // Arrange
            User profileDetails = createDefaultUser();
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.updateProfile(999L, profileDetails))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户不存在，ID: 999");
        }
    }

    // ==================== deleteUser 测试 ====================

    @Nested
    @DisplayName("deleteUser 方法")
    class DeleteUser {

        @Test
        @DisplayName("用户存在时删除用户")
        void should_deleteUser_when_userExists() {
            // Arrange
            when(userRepository.existsById(1L)).thenReturn(true);

            // Act
            userService.deleteUser(1L);

            // Assert
            verify(userRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("用户不存在时抛出异常")
        void should_throwException_when_userNotFound() {
            // Arrange
            when(userRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("用户不存在，ID: 999");
            verify(userRepository, never()).deleteById(any());
        }
    }

    // ==================== getOrCreateDefaultUser 测试 ====================

    @Nested
    @DisplayName("getOrCreateDefaultUser 方法")
    class GetOrCreateDefaultUser {

        @Test
        @DisplayName("已有用户时返回第一个用户")
        void should_returnFirstUser_when_usersExist() {
            // Arrange
            User user = createDefaultUser();
            when(userRepository.findAll()).thenReturn(List.of(user));

            // Act
            User result = userService.getOrCreateDefaultUser();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("无用户时创建默认用户")
        void should_createDefaultUser_when_noUsersExist() {
            // Arrange
            when(userRepository.findAll()).thenReturn(List.of());
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            // Act
            User result = userService.getOrCreateDefaultUser();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("admin");
            assertThat(result.getEmail()).isEqualTo("admin@example.com");
            assertThat(result.getBio()).isEqualTo("这是默认用户，欢迎编辑个人信息！");
            assertThat(result.getLocation()).isEqualTo("中国");
            verify(userRepository, times(1)).save(any(User.class));
        }
    }
}