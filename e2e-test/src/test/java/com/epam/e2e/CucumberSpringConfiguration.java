package com.epam.e2e;

import com.epam.e2e.config.E2EClientConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest(classes = E2EClientConfig.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CucumberSpringConfiguration {
}
