package com.epam.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SmokeTest {

    @Test
    @DisplayName("should verify e2e-test module compiles and runs successfully")
    void shouldRunSmokeTest() {
        assertTrue(true);
    }
}
