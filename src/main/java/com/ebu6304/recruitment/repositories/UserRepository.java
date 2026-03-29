package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户数据访问层（Repository）
 * 负责用户数据的持久化读写，数据存储在JSON文件中。
 *
 * <p>文件结构：
 * <ul>
 *   <li>data/users.json - 所有用户（User基类，含MO和TA）</li>
 *   <li>data/mo_profiles.json - MO详细档案</li>
 *   <li>data/ta_profiles.json - TA详细档案</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class UserRepository {

    /** MO档案数据文件路径 */
    private static final String MO_FILE = "data/mo_profiles.json";

    /** TA档案数据文件路径 */
    private static final String TA_FILE = "data/ta_profiles.json";

    // ==================== MO相关操作 ====================

    /**
     * 保存MO档案（新增或更新）。
     * 如果MO已存在（相同userId），则更新；否则新增。
     *
     * @param mo 要保存的MO对象
     */
    public void saveMO(ModuleOrganiser mo) {
        List<ModuleOrganiser> list = findAllMOs();
        // 查找是否已存在
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserId().equals(mo.getUserId())) {
                list.set(i, mo); // 更新
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(mo); // 新增
        }
        JsonFileUtil.writeList(MO_FILE, list);
    }

    /**
     * 根据用户ID查找MO。
     *
     * @param userId MO的用户ID
     * @return 包含MO的Optional，未找到则为空
     */
    public Optional<ModuleOrganiser> findMOById(String userId) {
        return findAllMOs().stream()
                .filter(mo -> mo.getUserId().equals(userId))
                .findFirst();
    }

    /**
     * 根据用户名查找MO。
     *
     * @param username 用户名
     * @return 包含MO的Optional，未找到则为空
     */
    public Optional<ModuleOrganiser> findMOByUsername(String username) {
        return findAllMOs().stream()
                .filter(mo -> mo.getUsername().equals(username))
                .findFirst();
    }

    /**
     * 获取所有MO列表。
     *
     * @return MO列表，文件不存在时返回空列表
     */
    public List<ModuleOrganiser> findAllMOs() {
        return JsonFileUtil.readList(MO_FILE, ModuleOrganiser.class);
    }

    /**
     * 根据模块代码查找MO。
     *
     * @param moduleCode 模块代码
     * @return 负责该模块的MO列表
     */
    public List<ModuleOrganiser> findMOsByModule(String moduleCode) {
        return findAllMOs().stream()
                .filter(mo -> moduleCode.equals(mo.getModuleCode()))
                .collect(Collectors.toList());
    }

    /**
     * 删除MO（按用户ID）。
     *
     * @param userId MO的用户ID
     * @return 删除成功返回true，未找到返回false
     */
    public boolean deleteMO(String userId) {
        List<ModuleOrganiser> list = findAllMOs();
        boolean removed = list.removeIf(mo -> mo.getUserId().equals(userId));
        if (removed) {
            JsonFileUtil.writeList(MO_FILE, list);
        }
        return removed;
    }

    // ==================== TA相关操作 ====================

    /**
     * 保存TA档案（新增或更新）。
     *
     * @param ta 要保存的TA对象
     */
    public void saveTA(TA ta) {
        List<TA> list = findAllTAs();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserId().equals(ta.getUserId())) {
                list.set(i, ta);
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(ta);
        }
        JsonFileUtil.writeList(TA_FILE, list);
    }

    /**
     * 根据用户ID查找TA。
     *
     * @param userId TA的用户ID
     * @return 包含TA的Optional，未找到则为空
     */
    public Optional<TA> findTAById(String userId) {
        return findAllTAs().stream()
                .filter(ta -> ta.getUserId().equals(userId))
                .findFirst();
    }

    /**
     * 根据用户名查找TA。
     *
     * @param username 用户名
     * @return 包含TA的Optional，未找到则为空
     */
    public Optional<TA> findTAByUsername(String username) {
        return findAllTAs().stream()
                .filter(ta -> ta.getUsername().equals(username))
                .findFirst();
    }

    /**
     * 获取所有TA列表。
     *
     * @return TA列表
     */
    public List<TA> findAllTAs() {
        return JsonFileUtil.readList(TA_FILE, TA.class);
    }

    /**
     * 根据技能筛选TA（不区分大小写）。
     *
     * @param skill 技能名称
     * @return 具备该技能的TA列表
     */
    public List<TA> findTAsBySkill(String skill) {
        return findAllTAs().stream()
                .filter(ta -> ta.hasSkill(skill))
                .collect(Collectors.toList());
    }

    /**
     * 根据最低GPA筛选TA。
     *
     * @param minGpa 最低GPA要求
     * @return GPA达标的TA列表
     */
    public List<TA> findTAsByMinGpa(double minGpa) {
        return findAllTAs().stream()
                .filter(ta -> ta.getGpa() >= minGpa)
                .collect(Collectors.toList());
    }

    /**
     * 删除TA（按用户ID）。
     *
     * @param userId TA的用户ID
     * @return 删除成功返回true
     */
    public boolean deleteTA(String userId) {
        List<TA> list = findAllTAs();
        boolean removed = list.removeIf(ta -> ta.getUserId().equals(userId));
        if (removed) {
            JsonFileUtil.writeList(TA_FILE, list);
        }
        return removed;
    }

    // ==================== 通用用户操作 ====================

    /**
     * 根据用户名查找用户（先查MO，再查TA）。
     * 用于登录验证。
     *
     * @param username 用户名
     * @return 包含User的Optional，未找到则为空
     */
    public Optional<User> findUserByUsername(String username) {
        // 先查MO
        Optional<ModuleOrganiser> mo = findMOByUsername(username);
        if (mo.isPresent()) return Optional.of(mo.get());
        // 再查TA
        Optional<TA> ta = findTAByUsername(username);
        if (ta.isPresent()) return Optional.of(ta.get());
        return Optional.empty();
    }

    /**
     * 检查用户名是否已被使用。
     *
     * @param username 用户名
     * @return 已使用返回true
     */
    public boolean usernameExists(String username) {
        return findUserByUsername(username).isPresent();
    }

    /**
     * 检查邮箱是否已被使用。
     *
     * @param email 邮箱地址
     * @return 已使用返回true
     */
    public boolean emailExists(String email) {
        boolean inMO = findAllMOs().stream()
                .anyMatch(mo -> email.equals(mo.getEmail()));
        boolean inTA = findAllTAs().stream()
                .anyMatch(ta -> email.equals(ta.getEmail()));
        return inMO || inTA;
    }
}
