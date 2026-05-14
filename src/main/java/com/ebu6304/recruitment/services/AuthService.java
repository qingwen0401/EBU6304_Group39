package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.utils.IdGenerator;
import com.ebu6304.recruitment.utils.PasswordUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证服务（Authentication Service）
 * 处理用户注册、登录、登出等认证相关业务逻辑。
 *
 * <p>安全机制：
 * <ul>
 *   <li>密码使用SHA-256加盐哈希存储</li>
 *   <li>登录成功后生成会话Token（简单UUID，非JWT）</li>
 *   <li>Token存储在内存Map中（重启后失效）</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class AuthService {

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /**
     * 内存会话存储：token → User对象
     * 注意：这是简化实现，生产环境应使用持久化会话或JWT
     */
    private final Map<String, User> sessionStore;

    /**
     * 构造方法，注入依赖。
     *
     * @param userRepository 用户数据访问层
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.sessionStore = new HashMap<>();
        // 自动初始化默认 Admin 账户（首次启动时创建）
        initDefaultAdmin();
        initDefaultMO();
    }
    /**
     * 初始化默认MO账户。
     * 默认账户：用户名 test_mo，密码 Test1234
     * 仅在没有任何 MO 账户时自动创建。
     */
    private void initDefaultMO() {
        try {
            if (userRepository.findAllMOs().isEmpty()) {
                registerMO("test_mo", "Test1234", "mo@bupt.edu.cn",
                        "Test MO", "CS", "EBU6304", "Software Engineering");
                System.out.println("[AuthService] Default MO created: test_mo / Test1234");
            } else {
                System.out.println("[AuthService] MO list not empty, skip default MO creation.");
            }
        } catch (Exception e) {
            System.err.println("[AuthService] initDefaultMO failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 初始化默认管理员账户。
     * 默认账户：用户名 admin，密码 admin123456
     * 仅在没有任何 Admin 账户时自动创建。
     */
    private void initDefaultAdmin() {
        if (userRepository.findAllAdmins().isEmpty()) {
            String adminId   = "ADMIN_001";
            String passHash  = PasswordUtil.hashPassword("admin123456");
            User admin = new User(adminId, "admin", passHash,
                    "admin@system.edu", "ADMIN", "System Administrator");
            userRepository.saveAdmin(admin);
        }
    }
    // ==================== 注册 ====================

    /**
     * 注册新的MO账户。
     *
     * @param username   用户名（唯一）
     * @param password   明文密码
     * @param email      邮箱（唯一）
     * @param fullName   真实姓名
     * @param department 所属院系
     * @param moduleCode 模块代码
     * @param moduleName 模块名称
     * @return 注册成功的MO对象
     * @throws IllegalArgumentException 用户名或邮箱已存在时抛出
     */
    public ModuleOrganiser registerMO(String username, String password, String email,
                                      String fullName, String department,
                                      String moduleCode, String moduleName) {
        // 验证用户名唯一性
        if (userRepository.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        // 验证邮箱唯一性
        if (userRepository.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        // 验证密码强度
        validatePassword(password);

        String moId = IdGenerator.generateMOId();
        String passwordHash = PasswordUtil.hashPassword(password);

        ModuleOrganiser mo = new ModuleOrganiser(
                moId, username, passwordHash, email, fullName,
                department, moduleCode, moduleName
        );
        userRepository.saveMO(mo);
        return mo;
    }

    /**
     * 注册新的TA账户。
     *
     * @param username   用户名（唯一）
     * @param password   明文密码
     * @param email      邮箱（唯一）
     * @param fullName   真实姓名
     * @param studentId  学号
     * @param department 所属院系
     * @param major      专业
     * @return 注册成功的TA对象
     * @throws IllegalArgumentException 用户名或邮箱已存在时抛出
     */
    public TA registerTA(String username, String password, String email,
                         String fullName, String studentId,
                         String department, String major) {
        if (userRepository.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        validatePassword(password);

        String taId = IdGenerator.generateTAId();
        String passwordHash = PasswordUtil.hashPassword(password);

        TA ta = new TA(taId, username, passwordHash, email, fullName,
                studentId, department, major);
        userRepository.saveTA(ta);
        return ta;
    }

    // ==================== 登录 ====================

    /**
     * 用户登录。
     * 验证用户名和密码，成功后生成会话Token。
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 会话Token字符串
     * @throws IllegalArgumentException 用户名不存在或密码错误时抛出
     */
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();

        // 验证账户是否激活
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        // 验证密码
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // 生成会话Token
        String token = generateToken(user.getUserId());
        sessionStore.put(token, user);
        return token;
    }

    // ==================== 登出 ====================

    /**
     * 用户登出，清除会话Token。
     *
     * @param token 会话Token
     */
    public void logout(String token) {
        sessionStore.remove(token);
    }

    // ==================== Token验证 ====================

    /**
     * 根据Token获取当前登录用户。
     *
     * @param token 会话Token
     * @return 包含User的Optional，Token无效则为空
     */
    public Optional<User> getUserByToken(String token) {
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessionStore.get(token));
    }

    /**
     * 验证Token是否有效。
     *
     * @param token 会话Token
     * @return 有效返回true
     */
    public boolean isValidToken(String token) {
        return token != null && sessionStore.containsKey(token);
    }

    /**
     * 验证Token并检查角色权限。
     *
     * @param token        会话Token
     * @param requiredRole 所需角色（"MO", "TA", "ADMIN"）
     * @return 有权限返回true
     */
    public boolean hasRole(String token, String requiredRole) {
        Optional<User> userOpt = getUserByToken(token);
        return userOpt.isPresent() && requiredRole.equals(userOpt.get().getRole());
    }

    // ==================== 密码管理 ====================

    /**
     * 修改密码。
     *
     * @param token       当前会话Token
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws IllegalArgumentException 旧密码错误或新密码不符合要求时抛出
     */
    public void changePassword(String token, String oldPassword, String newPassword) {
        Optional<User> userOpt = getUserByToken(token);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid session");
        }

        User user = userOpt.get();
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        validatePassword(newPassword);
        String newHash = PasswordUtil.hashPassword(newPassword);
        user.setPasswordHash(newHash);

        // 根据角色保存到对应文件
        if ("MO".equals(user.getRole())) {
            userRepository.saveMO((ModuleOrganiser) user);
        } else if ("TA".equals(user.getRole())) {
            userRepository.saveTA((TA) user);
        } else if ("ADMIN".equals(user.getRole())) {
            userRepository.saveAdmin(user);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 生成会话Token（基于用户ID和时间戳的简单Token）。
     *
     * @param userId 用户ID
     * @return Token字符串
     */
    private String generateToken(String userId) {
        return userId + "_" + System.currentTimeMillis() + "_"
                + (int)(Math.random() * 100000);
    }

    /**
     * 验证密码强度（至少6位）。
     *
     * @param password 明文密码
     * @throws IllegalArgumentException 密码不符合要求时抛出
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters long");
        }
    }
}
