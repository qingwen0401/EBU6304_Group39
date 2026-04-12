package com.ebu6304.recruitment.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 覆盖 ModuleOrganiser 实体方法：addPostedJob 去重、removePostedJob、ownsJob。

class ModuleOrganiserTest {

    @Test
    void addPostedJobDeduplicates() {
        ModuleOrganiser mo = new ModuleOrganiser();
        mo.addPostedJob("JOB1");
        mo.addPostedJob("JOB1");
        mo.addPostedJob("JOB2");
        assertEquals(Arrays.asList("JOB1", "JOB2"), mo.getPostedJobIds());
    }

    @Test
    void removePostedJob() {
        ModuleOrganiser mo = new ModuleOrganiser();
        mo.setPostedJobIds(new ArrayList<>(Arrays.asList("A", "B")));
        mo.removePostedJob("A");
        assertEquals(Arrays.asList("B"), mo.getPostedJobIds());
    }

    @Test
    void ownsJob() {
        ModuleOrganiser mo = new ModuleOrganiser();
        mo.addPostedJob("X");
        assertTrue(mo.ownsJob("X"));
        assertFalse(mo.ownsJob("Y"));
    }
}
