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
import static org.mockito.Mockito.*;

/**
 * {@link UserService} 单元测试。
 * <p>
 * 技术栈：JUnit 5 + Mockito + AssertJ（遵循项目 spring-boot-starter-test 约定）。
 * Mock 策略：仅 Mock {@link UserRepository}（外部依赖），值对象 {@link User} 直接 new。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "zhangsan";
    private static final String TEST_EMAIL = "zhangsan@example.com";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ==================== createUser 测试 ====================

    @Test
    @DisplayName("创建用户：用户名和邮箱均不重复时，应成功保存并返回用户")
    void should_createUser_when_usernameAndEmailAreUnique() {
        // Arrange
        User newUser = buildUser(null, TEST_USERNAME, TEST_EMAIL);
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).as("用户名应与输入一致").isEqualTo(TEST_USERNAME);
        assertThat(result.getEmail()).as("邮箱应与输入一致").isEqualTo(TEST_EMAIL);
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    @DisplayName("创建用户：用户名已存在时，应抛出 IllegalArgumentException")
    void should_throwException_when_usernameAlreadyExists() {
        // Arrange
        User newUser = buildUser(null, TEST_USERNAME, TEST_EMAIL);
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(TEST_USERNAME)
                .hasMessageContaining("已存在");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("创建用户：邮箱已被注册时，应抛出 IllegalArgumentException")
    void should_throwException_when_emailAlreadyRegistered() {
        // Arrange
        User newUser = buildUser(null, TEST_USERNAME, TEST_EMAIL);
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(TEST_EMAIL)
                .hasMessageContaining("已被注册");
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== getUserById 测试 ====================

    @Test
    @DisplayName("根据ID查询用户：用户存在时，应返回包含该用户的 Optional")
    void should_returnUser_when_idExists() {
        // Arrange
        User existingUser = buildUser(TEST_USER_ID, TEST_USERNAME, TEST_EMAIL);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));

        // Act
        Optional<User> result = userService.getUserById(TEST_USER_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(TEST_USER_ID);
        assertThat(result.get().getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("根据ID查询用户：用户不存在时，应返回空 Optional")
    void should_returnEmpty_when_idDoesNotExist() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(TEST_USER_ID);

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== getAllUsers 测试 ====================

    @Test
    @DisplayName("查询全部用户：应返回用户列表")
    void should_returnAllUsers() {
        // Arrange
        User user1 = buildUser(1L, "user1", "user1@example.com");
        User user2 = buildUser(2L, "user2", "user2@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUsername).containsExactly("user1", "user2");
    }

    @Test
    @DisplayName("查询全部用户：无数据时应返回空列表")
    void should_returnEmptyList_when_noUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== updateProfile 测试 ====================

    @Test
    @DisplayName("更新用户资料：用户存在且用户名/邮箱未与他人冲突时，应成功更新")
    void should_updateProfile_when_userExistsAndNoConflict() {
        // Arrange
        User existingUser = buildUser(TEST_USER_ID, TEST_USERNAME, TEST_EMAIL);
        User profileDetails = buildUser(null, "lisi", "lisi@example.com");
        profileDetails.setPhone("13800000000");
        profileDetails.setBio("更新后的简介");
        profileDetails.setLocation("上海");
        profileDetails.setAvatarUrl("http://example.com/avatar.png");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("lisi")).thenReturn(false);
        when(userRepository.existsByEmail("lisi@example.com")).thenReturn(false);
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = userService.updateProfile(TEST_USER_ID, profileDetails);

        // Assert
        assertThat(result.getUsername()).as("用户名应已更新").isEqualTo("lisi");
        assertThat(result.getEmail()).as("邮箱应已更新").isEqualTo("lisi@example.com");
        assertThat(result.getPhone()).isEqualTo("13800000000");
        assertThat(result.getBio()).isEqualTo("更新后的简介");
        assertThat(result.getLocation()).isEqualTo("上海");
        assertThat(result.getAvatarUrl()).isEqualTo("http://example.com/avatar.png");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("更新用户资料：用户不存在时，应抛出 IllegalArgumentException")
    void should_throwException_when_userNotFoundDuringUpdate() {
        // Arrange
        User profileDetails = buildUser(null, "lisi", "lisi@example.com");
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(TEST_USER_ID, profileDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在")
                .hasMessageContaining(String.valueOf(TEST_USER_ID));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("更新用户资料：新用户名与他人重复时，应抛出 IllegalArgumentException")
    void should_throwException_when_newUsernameConflictsWithExistingUser() {
        // Arrange
        User existingUser = buildUser(TEST_USER_ID, TEST_USERNAME, TEST_EMAIL);
        User profileDetails = buildUser(null, "conflict_user", TEST_EMAIL);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("conflict_user")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(TEST_USER_ID, profileDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("conflict_user")
                .hasMessageContaining("已存在");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("更新用户资料：新邮箱已被他人注册时，应抛出 IllegalArgumentException")
    void should_throwException_when_newEmailConflictsWithExistingUser() {
        // Arrange
        User existingUser = buildUser(TEST_USER_ID, TEST_USERNAME, TEST_EMAIL);
        User profileDetails = buildUser(null, TEST_USERNAME, "conflict@example.com");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("conflict@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(TEST_USER_ID, profileDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("conflict@example.com")
                .hasMessageContaining("已被注册");
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== deleteUser 测试 ====================

    @Test
    @DisplayName("删除用户：用户存在时，应成功删除")
    void should_deleteUser_when_userExists() {
        // Arrange
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);

        // Act
        userService.deleteUser(TEST_USER_ID);

        // Assert
        verify(userRepository, times(1)).deleteById(TEST_USER_ID);
    }

    @Test
    @DisplayName("删除用户：用户不存在时，应抛出 IllegalArgumentException")
    void should_throwException_when_deleteNonExistentUser() {
        // Arrange
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(TEST_USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在")
                .hasMessageContaining(String.valueOf(TEST_USER_ID));
        verify(userRepository, never()).deleteById(any());
    }

    // ==================== getOrCreateDefaultUser 测试 ====================

    @Test
    @DisplayName("获取默认用户：已有用户数据时，应返回第一个用户")
    void should_returnFirstUser_when_usersAlreadyExist() {
        // Arrange
        User firstUser = buildUser(1L, "admin", "admin@example.com");
        User secondUser = buildUser(2L, "user2", "user2@example.com");
        when(userRepository.findAll()).thenReturn(List.of(firstUser, secondUser));

        // Act
        User result = userService.getOrCreateDefaultUser();

        // Assert
        assertThat(result.getId()).as("应返回第一个用户").isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("admin");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("获取默认用户：无用户数据时，应创建并返回默认用户")
    void should_createDefaultUser_when_noUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        User result = userService.getOrCreateDefaultUser();

        // Assert
        assertThat(result.getUsername()).as("默认用户名应为 admin").isEqualTo("admin");
        assertThat(result.getEmail()).as("默认邮箱应为 admin@example.com").isEqualTo("admin@example.com");
        assertThat(result.getBio()).isEqualTo("这是默认用户，欢迎编辑个人信息！");
        assertThat(result.getLocation()).isEqualTo("中国");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ==================== 测试数据构造方法 ====================

    /**
     * 构造测试用 User 对象。
     *
     * @param id       用户ID，新建场景传 null
     * @param username 用户名
     * @param email    邮箱
     * @return 构造完成的 User 实例
     */
    private User buildUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
