package com.example.myapp.config;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * 种子数据初始化器，在应用启动时插入演示用户（含人员维度字段）。
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("用户数据已存在，跳过种子数据初始化");
            return;
        }
        createUser("alice", "alice@example.com", "开发", "高级", "技术部");
        createUser("bob", "bob@example.com", "测试", "中级", "质量部");
        createUser("carol", "carol@example.com", "产品", "初级", "产品部");
        log.info("种子数据初始化完成，共插入 3 个用户");
    }

    private void createUser(String username, String email, String userType,
                           String userLevel, String department) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setUserType(userType);
        user.setUserLevel(userLevel);
        user.setDepartment(department);
        userRepository.save(user);
    }
}
