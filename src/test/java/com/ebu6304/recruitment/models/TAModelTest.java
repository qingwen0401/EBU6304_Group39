package com.ebu6304.recruitment.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
覆盖 TA 实体上的业务方法，不依赖数据库或 Servlet：

addSkill：去重、trim、忽略空串
hasSkill：大小写不敏感
removeSkill
addApplication：去重
hasAvailableHours / addWorkHours：与 maxWeeklyHours 的配合
 */
class TAModelTest {

    @Test
    void addSkillTrimsAndDeduplicates() {
        TA ta = new TA();
        ta.addSkill("  Java  ");
        ta.addSkill("Java");
        ta.addSkill("Python");
        assertEquals(Arrays.asList("Java", "Python"), ta.getSkills());
    }

    @Test
    void addSkillIgnoresNullOrBlank() {
        TA ta = new TA();
        ta.addSkill(null);
        ta.addSkill("   ");
        ta.addSkill("\t");
        assertTrue(ta.getSkills().isEmpty());
    }

    @Test
    void hasSkillMatchesCaseInsensitive() {
        TA ta = new TA();
        ta.setSkills(new ArrayList<>(Arrays.asList("UML")));
        assertTrue(ta.hasSkill("uml"));
        assertTrue(ta.hasSkill(" UML "));
        assertFalse(ta.hasSkill("Java"));
    }

    @Test
    void removeSkill() {
        TA ta = new TA();
        ta.setSkills(new ArrayList<>(Arrays.asList("A", "B")));
        ta.removeSkill("A");
        assertEquals(Arrays.asList("B"), ta.getSkills());
    }

    @Test
    void addApplicationDeduplicates() {
        TA ta = new TA();
        ta.addApplication("APP1");
        ta.addApplication("APP1");
        ta.addApplication("APP2");
        assertEquals(Arrays.asList("APP1", "APP2"), ta.getApplicationIds());
    }

    @Test
    void hasAvailableHoursRespectsMaxWeeklyHours() {
        TA ta = new TA();
        ta.setCurrentWeeklyHours(15);
        ta.setMaxWeeklyHours(20);
        assertTrue(ta.hasAvailableHours(5));
        assertFalse(ta.hasAvailableHours(6));
    }

    @Test
    void addWorkHoursAccumulates() {
        TA ta = new TA();
        ta.setCurrentWeeklyHours(3);
        ta.addWorkHours(4);
        assertEquals(7, ta.getCurrentWeeklyHours());
    }
}
