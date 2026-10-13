package com.kisanmitra.config;

import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GovernmentSchemeSeederTest {

    @Autowired
    private GovernmentSchemeRepository schemeRepository;

    @Autowired
    private GovernmentSchemeSeeder seeder;

    @Test
    @DisplayName("Seeder should seed initial schemes when database is empty")
    void testSeeder_InitialRun() {
        assertTrue(schemeRepository.count() >= 7, "Seeder should populate at least 7 verified schemes");
    }

    @Test
    @DisplayName("Repeated seeder execution should not duplicate schemes or overwrite administrator edits")
    void testSeeder_RepeatedRun_PreservesEdits() throws Exception {
        long initialCount = schemeRepository.count();

        // Simulate admin edit
        Optional<GovernmentScheme> schemeOpt = schemeRepository.findByNameIgnoreCase("Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)");
        assertTrue(schemeOpt.isPresent());
        GovernmentScheme scheme = schemeOpt.get();
        scheme.setBenefits("Custom Admin Edited Benefit Text");
        schemeRepository.save(scheme);

        // Run seeder again
        seeder.run();

        // Assert count remains unchanged and admin edit is preserved
        assertEquals(initialCount, schemeRepository.count(), "Seeder must not duplicate schemes on rerun");
        GovernmentScheme reFetched = schemeRepository.findByNameIgnoreCase("Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)").orElseThrow();
        assertEquals("Custom Admin Edited Benefit Text", reFetched.getBenefits(), "Seeder must preserve existing administrator edits");
    }
}
