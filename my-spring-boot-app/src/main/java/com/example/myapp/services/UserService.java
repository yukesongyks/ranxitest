package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名 '" + user.getUsername() + "' 已存在");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱 '" + user.getEmail() + "' 已被注册");
        }
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateProfile(Long id, User profileDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + id));

        if (!user.getUsername().equals(profileDetails.getUsername()) &&
            userRepository.existsByUsername(profileDetails.getUsername())) {
            throw new IllegalArgumentException("用户名 '" + profileDetails.getUsername() + "' 已存在");
        }
        if (!user.getEmail().equals(profileDetails.getEmail()) &&
            userRepository.existsByEmail(profileDetails.getEmail())) {
            throw new IllegalArgumentException("邮箱 '" + profileDetails.getEmail() + "' 已被注册");
        }

        user.setUsername(profileDetails.getUsername());
        user.setEmail(profileDetails.getEmail());
        user.setPhone(profileDetails.getPhone());
        user.setBio(profileDetails.getBio());
        user.setLocation(profileDetails.getLocation());
        user.setAvatarUrl(profileDetails.getAvatarUrl());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在，ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * 获取或初始化默认用户（用于演示，实际项目应接入认证系统）
     */
    public User getOrCreateDefaultUser() {
        return userRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    User defaultUser = new User();
                    defaultUser.setUsername("admin");
                    defaultUser.setPassword(passwordEncoder.encode("admin123"));
                    defaultUser.setEmail("admin@example.com");
                    defaultUser.setBio("这是默认用户，欢迎编辑个人信息！");
                    defaultUser.setLocation("中国");
                    return userRepository.save(defaultUser);
                });
    }
}