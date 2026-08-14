package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User createSampleUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone("13800138000");
        user.setBio("Test bio");
        user.setLocation("China");
        return user;
    }

    // ==================== createUser 测试 ====================

    @Test
    void should_createUser_when_usernameAndEmailAreUnique() {
        // Arrange
        User newUser = createSampleUser(null, "newuser", "new@example.com");
        User savedUser = createSampleUser(1L, "newuser", "new@example.com");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(newUser);
    }

    @Test
    void should_throwException_when_createUserWithDuplicateUsername() {
        // Arrange
        User newUser = createSampleUser(null, "existing", "new@example.com");
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existing")
                .hasMessageContaining("已存在");
        verify(userRepository).existsByUsername("existing");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_throwException_when_createUserWithDuplicateEmail() {
        // Arrange
        User newUser = createSampleUser(null, "unique", "taken@example.com");
        when(userRepository.existsByUsername("unique")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taken@example.com")
                .hasMessageContaining("已被注册");
        verify(userRepository).existsByUsername("unique");
        verify(userRepository).existsByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    private String anyString() {
        return any();
    }

    // ==================== getUserById 测试 ====================

    @Test
    void should_returnUser_when_getUserByIdWithExistingId() {
        // Arrange
        User expected = createSampleUser(1L, "user1", "user1@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(expected));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
        verify(userRepository).findById(1L);
    }

    @Test
    void should_returnEmpty_when_getUserByIdWithNonExistingId() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findById(999L);
    }

    // ==================== getAllUsers 测试 ====================

    @Test
    void should_returnAllUsers() {
        // Arrange
        List<User> expected = Arrays.asList(
                createSampleUser(1L, "user1", "user1@example.com"),
                createSampleUser(2L, "user2", "user2@example.com"));
        when(userRepository.findAll()).thenReturn(expected);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUsername).containsExactly("user1", "user2");
        verify(userRepository).findAll();
    }

    @Test
    void should_returnEmptyList_when_noUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    // ==================== updateProfile 测试 ====================

    @Test
    void should_updateProfile_when_validRequest() {
        // Arrange
        User existingUser = createSampleUser(1L, "oldname", "old@example.com");
        User updateDetails = createSampleUser(null, "newname", "new@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = userService.updateProfile(1L, updateDetails);

        // Assert
        assertThat(result.getUsername()).isEqualTo("newname");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getPhone()).isEqualTo("13800138000");
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    void should_throwException_when_updateProfileWithNonExistingUser() {
        // Arrange
        User updateDetails = createSampleUser(null, "newname", "new@example.com");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(999L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在");
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_throwException_when_updateProfileWithDuplicateUsername() {
        // Arrange
        User existingUser = createSampleUser(1L, "oldname", "old@example.com");
        User updateDetails = createSampleUser(null, "takenname", "new@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("takenname")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(1L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("takenname")
                .hasMessageContaining("已存在");
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("takenname");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_throwException_when_updateProfileWithDuplicateEmail() {
        // Arrange
        User existingUser = createSampleUser(1L, "oldname", "old@example.com");
        User updateDetails = createSampleUser(null, "oldname", "taken@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(1L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taken@example.com")
                .hasMessageContaining("已被注册");
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_skipDuplicateCheck_when_usernameUnchanged() {
        // Arrange
        User existingUser = createSampleUser(1L, "sameuser", "old@example.com");
        User updateDetails = createSampleUser(null, "sameuser", "new@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = userService.updateProfile(1L, updateDetails);

        // Assert
        assertThat(result.getUsername()).isEqualTo("sameuser");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository).save(existingUser);
    }

    // ==================== deleteUser 测试 ====================

    @Test
    void should_deleteUser_when_idExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void should_throwException_when_deleteNonExistingUser() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在");
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    // ==================== getOrCreateDefaultUser 测试 ====================

    @Test
    void should_returnFirstUser_when_usersExist() {
        // Arrange
        User existingUser = createSampleUser(1L, "admin", "admin@example.com");
        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser));

        // Act
        User result = userService.getOrCreateDefaultUser();

        // Assert
        assertThat(result.getUsername()).isEqualTo("admin");
        verify(userRepository).findAll();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_createDefaultUser_when_noUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        User savedUser = createSampleUser(1L, "admin", "admin@example.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.getOrCreateDefaultUser();

        // Assert
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getEmail()).isEqualTo("admin@example.com");
        assertThat(result.getBio()).isEqualTo("这是默认用户，欢迎编辑个人信息！");
        assertThat(result.getLocation()).isEqualTo("中国");
        verify(userRepository).findAll();
        verify(userRepository).save(any(User.class));
    }
}