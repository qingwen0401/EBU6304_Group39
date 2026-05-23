package com.ebu6304.recruitment.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI匹配服务测试
 *
 * 注意：这些测试需要有效的DeepSeek API密钥才能运行
 * 如果遇到HTTP 402错误，说明API密钥余额不足
 */
public class AIMatchingServiceTest {

    @Test
    
    @Disabled("手动测试 - 需要在代码中填入API密钥后启用")
    public void testMatchSkills_withValidApiKey() {
        AIMatchingService service = new AIMatchingService();

        // 替换为你的有效API密钥
        String apiKey = "sk-3577442c0da1414cba5283b513c95b23";

        // 职位信息
        String jobRequirements = "Looking for a TA with strong Java and database skills";
        String jobTitle = "Java TA Position";
        List<String> requiredSkills = Arrays.asList("Java", "MySQL", "Spring Boot");

        // TA信息
        String taName = "Test Student";
        String taBio = "Computer Science student with 2 years Java experience";
        List<String> taSkills = Arrays.asList("Java", "Python", "MySQL");
        double taGpa = 3.8;
        String coverLetter = "I am passionate about teaching Java and have strong database knowledge.";

        try {
            AIMatchingService.AIMatchingResult result = service.matchSkills(
                apiKey, jobRequirements, jobTitle, requiredSkills,
                taName, taBio, taSkills, taGpa, coverLetter
            );

            assertNotNull(result);
            assertTrue(result.getMatchingScore() >= 0 && result.getMatchingScore() <= 100);
            assertNotNull(result.getMatchedSkills());
            assertNotNull(result.getReason());

            System.out.println("=== AI Matching Result ===");
            System.out.println("Matching Score: " + result.getMatchingScore());
            System.out.println("Matched Skills: " + result.getMatchedSkills());
            System.out.println("Reason: " + result.getReason());

        } catch (Exception e) {
            if (e.getMessage().contains("402")) {
                System.err.println("API密钥余额不足 - 请充值或使用新的API密钥");
            } else if (e.getMessage().contains("401")) {
                System.err.println("API密钥无效 - 请检查密钥是否正确");
            }
            throw e;
        }
    }

    @Test
    public void testMatchSkills_withInvalidApiKey_shouldThrowException() {
        AIMatchingService service = new AIMatchingService();

        String invalidApiKey = "sk-invalid-key";
        String jobRequirements = "Test requirements";
        String jobTitle = "Test Job";
        List<String> requiredSkills = Arrays.asList("Java");
        String taName = "Test Student";
        String taBio = "Test bio";
        List<String> taSkills = Arrays.asList("Java");
        double taGpa = 3.5;
        String coverLetter = "Test letter";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.matchSkills(
                invalidApiKey, jobRequirements, jobTitle, requiredSkills,
                taName, taBio, taSkills, taGpa, coverLetter
            );
        });

        assertTrue(exception.getMessage().contains("AI matching failed"));
    }

    @Test
    public void testMatchSkills_withNullApiKey_shouldThrowException() {
        AIMatchingService service = new AIMatchingService();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.matchSkills(
                null, "requirements", "title", Arrays.asList("Java"),
                "name", "bio", Arrays.asList("Java"), 3.5, "letter"
            );
        });

        assertEquals("DeepSeek API key is required", exception.getMessage());
    }

    @Test
    public void testMatchSkills_withEmptyApiKey_shouldThrowException() {
        AIMatchingService service = new AIMatchingService();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.matchSkills(
                "   ", "requirements", "title", Arrays.asList("Java"),
                "name", "bio", Arrays.asList("Java"), 3.5, "letter"
            );
        });

        assertEquals("DeepSeek API key is required", exception.getMessage());
    }

    /**
     * 手动测试脚本 - 在命令行运行
     *
     * 使用方法：
     * 1. 将下面的API_KEY替换为你的有效DeepSeek API密钥
     * 2. 运行: mvn test -Dtest=AIMatchingServiceTest#manualTest
     */
    @Test
    @Disabled("手动测试 - 需要在代码中填入API密钥后启用")
    public void manualTest() {
        // ========== 配置区 ==========
        String API_KEY = "sk-your-deepseek-api-key-here";  // 替换为你的API密钥

        // 职位信息
        String jobTitle = "Software Engineering TA";
        String jobRequirements = "We are looking for a TA to assist with our Software Engineering course. " +
                                "The ideal candidate should have strong programming skills in Java, " +
                                "experience with web development, and good communication skills.";
        List<String> requiredSkills = Arrays.asList("Java", "Spring Boot", "MySQL", "Git");

        // TA信息
        String taName = "Alice Johnson";
        String taBio = "Third-year Computer Science student with passion for teaching. " +
                      "I have completed advanced courses in software engineering and databases.";
        List<String> taSkills = Arrays.asList("Java", "Python", "Spring Boot", "MySQL", "React");
        double taGpa = 3.85;
        String coverLetter = "I am very interested in this TA position. I have strong Java skills " +
                           "and have worked on several Spring Boot projects. I believe I can help " +
                           "students understand complex software engineering concepts.";
        // ========== 配置区结束 ==========

        AIMatchingService service = new AIMatchingService();

        System.out.println("=== 开始AI匹配测试 ===\n");
        System.out.println("职位: " + jobTitle);
        System.out.println("要求技能: " + requiredSkills);
        System.out.println("TA姓名: " + taName);
        System.out.println("TA技能: " + taSkills);
        System.out.println("TA GPA: " + taGpa);
        System.out.println("\n正在调用DeepSeek API...\n");

        try {
            AIMatchingService.AIMatchingResult result = service.matchSkills(
                API_KEY, jobRequirements, jobTitle, requiredSkills,
                taName, taBio, taSkills, taGpa, coverLetter
            );

            System.out.println("=== 匹配结果 ===");
            System.out.println("匹配分数: " + result.getMatchingScore() + "/100");
            System.out.println("匹配技能: " + result.getMatchedSkills());
            System.out.println("推荐理由: " + result.getReason());
            System.out.println("\n测试成功！");

        } catch (RuntimeException e) {
            System.err.println("\n=== 测试失败 ===");
            System.err.println("错误信息: " + e.getMessage());

            if (e.getMessage().contains("402")) {
                System.err.println("\n问题诊断: API密钥余额不足");
                System.err.println("解决方案:");
                System.err.println("1. 访问 https://platform.deepseek.com/");
                System.err.println("2. 登录并充值账户");
                System.err.println("3. 或者创建新的API密钥");
            } else if (e.getMessage().contains("401")) {
                System.err.println("\n问题诊断: API密钥无效");
                System.err.println("解决方案:");
                System.err.println("1. 检查API密钥是否正确复制");
                System.err.println("2. 确认密钥格式为 sk-xxxxxxxx");
                System.err.println("3. 在DeepSeek平台重新生成密钥");
            } else if (e.getMessage().contains("timeout")) {
                System.err.println("\n问题诊断: 网络超时");
                System.err.println("解决方案:");
                System.err.println("1. 检查网络连接");
                System.err.println("2. 如果在中国大陆，可能需要配置代理");
            }

            throw e;
        }
    }
}
