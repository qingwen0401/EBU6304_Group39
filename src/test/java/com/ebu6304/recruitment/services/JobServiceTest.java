package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JobServiceTest {

    private FakeJobRepository jobRepository;
    private FakeUserRepository userRepository;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobRepository = new FakeJobRepository();
        userRepository = new FakeUserRepository();
        jobService = new JobService(jobRepository, userRepository);
    }

    @Test
    void postJob_shouldCreateAndSaveJobSuccessfully() {
        ModuleOrganiser mo = new ModuleOrganiser();
        mo.setUserId("MO001");
        mo.setFullName("Dr Smith");
        userRepository.moResult = Optional.of(mo);

        JobPosting result = jobService.postJob(
                "MO001",
                "EBU6304",
                "Software Engineering",
                "Lab TA",
                "Assist in labs",
                Arrays.asList("Java", "OOP"),
                8,
                2,
                "2026-04-30",
                "2026 Spring",
                "LAB_TA",
                3.5,
                15.0
        );

        assertNotNull(result);
        assertNotNull(jobRepository.savedJob);
        assertEquals(1, jobRepository.saveCount);

        JobPosting saved = jobRepository.savedJob;
        assertEquals("MO001", saved.getMoId());
        assertEquals("Lab TA", saved.getTitle());
        assertEquals("OPEN", saved.getStatus());
        assertEquals(2, saved.getVacancies());
        assertEquals(8, saved.getHoursPerWeek());
        assertEquals(15.0, saved.getHourlyRate());

        assertEquals(mo, userRepository.savedMo);
        assertEquals(1, userRepository.saveMOCount);
    }

    @Test
    void postJob_shouldThrowWhenMoNotFound() {
        userRepository.moResult = Optional.empty();

        assertThrows(IllegalArgumentException.class, () -> jobService.postJob(
                "MO001",
                "EBU6304",
                "Software Engineering",
                "Lab TA",
                "Assist in labs",
                Arrays.asList("Java"),
                8,
                2,
                "2026-04-30",
                "2026 Spring",
                "LAB_TA",
                3.5,
                15.0
        ));

        assertEquals(0, jobRepository.saveCount);
        assertNull(jobRepository.savedJob);
    }

    @Test
    void postJob_shouldThrowWhenTitleIsBlank() {
        ModuleOrganiser mo = new ModuleOrganiser();
        mo.setUserId("MO001");
        userRepository.moResult = Optional.of(mo);

        assertThrows(IllegalArgumentException.class, () -> jobService.postJob(
                "MO001",
                "EBU6304",
                "Software Engineering",
                "",
                "Assist in labs",
                Arrays.asList("Java"),
                8,
                2,
                "2026-04-30",
                "2026 Spring",
                "LAB_TA",
                3.5,
                15.0
        ));

        assertEquals(0, jobRepository.saveCount);
    }

    @Test
    void postJob_shouldThrowWhenHoursInvalid() {
        ModuleOrganiser mo = new ModuleOrganiser();
        mo.setUserId("MO001");
        userRepository.moResult = Optional.of(mo);

        assertThrows(IllegalArgumentException.class, () -> jobService.postJob(
                "MO001",
                "EBU6304",
                "Software Engineering",
                "Lab TA",
                "Assist in labs",
                Arrays.asList("Java"),
                0,
                2,
                "2026-04-30",
                "2026 Spring",
                "LAB_TA",
                3.5,
                15.0
        ));

        assertEquals(0, jobRepository.saveCount);
    }

    @Test
    void postJob_shouldThrowWhenVacanciesInvalid() {
        ModuleOrganiser mo = new ModuleOrganiser();
        mo.setUserId("MO001");
        userRepository.moResult = Optional.of(mo);

        assertThrows(IllegalArgumentException.class, () -> jobService.postJob(
                "MO001",
                "EBU6304",
                "Software Engineering",
                "Lab TA",
                "Assist in labs",
                Arrays.asList("Java"),
                8,
                0,
                "2026-04-30",
                "2026 Spring",
                "LAB_TA",
                3.5,
                15.0
        ));

        assertEquals(0, jobRepository.saveCount);
    }

    private static class FakeJobRepository extends JobRepository {
        private JobPosting savedJob;
        private int saveCount = 0;

        @Override
        public void save(JobPosting job) {
            this.savedJob = job;
            this.saveCount++;
        }
    }

    private static class FakeUserRepository extends UserRepository {
        private Optional<ModuleOrganiser> moResult = Optional.empty();
        private ModuleOrganiser savedMo;
        private int saveMOCount = 0;

        @Override
        public Optional<ModuleOrganiser> findMOById(String userId) {
            return moResult;
        }

        @Override
        public void saveMO(ModuleOrganiser mo) {
            this.savedMo = mo;
            this.saveMOCount++;
        }
    }
}