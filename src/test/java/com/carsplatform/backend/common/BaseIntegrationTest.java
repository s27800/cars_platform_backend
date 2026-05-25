package com.carsplatform.backend.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


/**
 * Base class for integration tests.
 */

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {
    protected void simpleEntityManagerFlushAndClear(jakarta.persistence.EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }
}
