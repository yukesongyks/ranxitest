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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link UserService} 单元测试。
 *
 * <p>覆盖核心业务逻辑：创建用户时的用户名/邮箱唯一性校验、
 * 更新个人信息的冲突检测、删除前存在性校验、
 * 以及 getOrCreateDefaultUser 的「有数据则取首个、无数据则初始化默认用户」分支。
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
        sampleUser.setUsername("admin");
        sampleUser.setEmail("admin@example.com");
        sampleUser.setPhone("13800138000");
        sampleUser.setBio("简介");
        sampleUser.setLocation("中国");
        sampleUser.setAvatarUrl("http://example.com/avatar.png");
    }

    // ------------------------------------------------------------------
    // createUser
    // ------------------------------------------------------------------

    @Test
    @DisplayName("createUser: 用户名和邮箱均不重复时正常保存")
    void createUser_shouldPersistWhenNoConflict() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");

        User result = userService.createUser(newUser);

        assertThat(result).isSameAs(newUser);
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("createUser: 用户名已存在时抛 IllegalArgumentException")
    void createUser_shouldThrowWhenUsernameExists() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        User duplicate = new User();
        duplicate.setUsername("admin");
        duplicate.setEmail("other@example.com");

        assertThatThrownBy(() -> userService.createUser(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("admin")
                .hasMessageContaining("已存在");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: 邮箱已被注册时抛 IllegalArgumentException")
    void createUser_shouldThrowWhenEmailExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);

        User duplicate = new User();
        duplicate.setUsername("newuser");
        duplicate.setEmail("admin@example.com");

        assertThatThrownBy(() -> userService.createUser(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("admin@example.com")
                .hasMessageContaining("已被注册");

        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------------------------------------------------
    // getUserById / getAllUsers
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getUserById: 存在时返回 Optional 包含用户")
    void getUserById_shouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result).isPresent().contains(sampleUser);
    }

    @Test
    @DisplayName("getUserById: 不存在时返回空 Optional")
    void getUserById_shouldReturnEmptyWhenNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllUsers: 委托 Repository 返回全部用户")
    void getAllUsers_shouldDelegateToRepository() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(sampleUser));

        List<User> result = userService.getAllUsers();

        assertThat(result).containsExactly(sampleUser);
    }

    // ------------------------------------------------------------------
    // updateProfile
    // ------------------------------------------------------------------

    @Test
    @DisplayName("updateProfile: 用户名和邮箱均未变时正常更新")
    void updateProfile_shouldUpdateWhenUsernameAndEmailUnchanged() {
        User details = new User();
        details.setUsername("admin");
        details.setEmail("admin@example.com");
        details.setPhone("13900139000");
        details.setBio("新简介");
        details.setLocation("上海");
        details.setAvatarUrl("http://example.com/new.png");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User result = userService.updateProfile(1L, details);

        assertThat(result).isSameAs(sampleUser);
        assertThat(sampleUser.getPhone()).isEqualTo("13900139000");
        assertThat(sampleUser.getBio()).isEqualTo("新简介");
        assertThat(sampleUser.getLocation()).isEqualTo("上海");
        assertThat(sampleUser.getAvatarUrl()).isEqualTo("http://example.com/new.png");
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    @DisplayName("updateProfile: 用户名变更且无冲突时正常更新")
    void updateProfile_shouldUpdateWhenUsernameChangedAndNoConflict() {
        User details = new User();
        details.setUsername("newadmin");
        details.setEmail("admin@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByUsername("newadmin")).thenReturn(false);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User result = userService.updateProfile(1L, details);

        assertThat(result.getUsername()).isEqualTo("newadmin");
        verify(userRepository).existsByUsername("newadmin");
    }

    @Test
    @DisplayName("updateProfile: 邮箱变更且无冲突时正常更新")
    void updateProfile_shouldUpdateWhenEmailChangedAndNoConflict() {
        User details = new User();
        details.setUsername("admin");
        details.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User result = userService.updateProfile(1L, details);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).existsByEmail("new@example.com");
    }

    @Test
    @DisplayName("updateProfile: 用户不存在时抛 IllegalArgumentException")
    void updateProfile_shouldThrowWhenUserNotFound() {
        User details = new User();
        details.setUsername("admin");
        details.setEmail("admin@example.com");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(999L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateProfile: 用户名变更为已存在名称时抛 IllegalArgumentException")
    void updateProfile_shouldThrowWhenNewUsernameConflicts() {
        User details = new User();
        details.setUsername("taken");
        details.setEmail("admin@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateProfile: 邮箱变更为已被注册邮箱时抛 IllegalArgumentException")
    void updateProfile_shouldThrowWhenNewEmailConflicts() {
        User details = new User();
        details.setUsername("admin");
        details.setEmail("taken@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(1L, details))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taken@example.com");

        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------------------------------------------------
    // deleteUser
    // ------------------------------------------------------------------

    @Test
    @DisplayName("deleteUser: 用户存在时正常删除")
    void deleteUser_shouldDeleteWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: 用户不存在时抛 IllegalArgumentException")
    void deleteUser_shouldThrowWhenNotExists() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在");

        verify(userRepository, never()).deleteById(any());
    }

    // ------------------------------------------------------------------
    // getOrCreateDefaultUser
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getOrCreateDefaultUser: 仓库已有用户时返回首个用户，不创建新用户")
    void getOrCreateDefaultUser_shouldReturnFirstUserWhenDataExists() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(sampleUser));

        User result = userService.getOrCreateDefaultUser();

        assertThat(result).isSameAs(sampleUser);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getOrCreateDefaultUser: 仓库无数据时创建并保存默认用户")
    void getOrCreateDefaultUser_shouldCreateDefaultWhenNoData() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = userService.getOrCreateDefaultUser();

        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getEmail()).isEqualTo("admin@example.com");
        assertThat(result.getBio()).isEqualTo("这是默认用户，欢迎编辑个人信息！");
        assertThat(result.getLocation()).isEqualTo("中国");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getOrCreateDefaultUser: 多用户时返回首个用户")
    void getOrCreateDefaultUser_shouldReturnFirstOfMultipleUsers() {
        User second = new User();
        second.setId(2L);
        second.setUsername("user2");
        when(userRepository.findAll())
                .thenReturn(List.of(sampleUser, second));

        User result = userService.getOrCreateDefaultUser();

        assertThat(result).isSameAs(sampleUser);
        assertThat(result.getId()).isEqualTo(1L);
    }
}
