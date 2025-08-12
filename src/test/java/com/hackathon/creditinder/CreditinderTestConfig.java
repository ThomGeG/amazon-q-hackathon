package com.hackathon.creditinder;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.hackathon.creditinder.service.LoanApplicationService;

/**
 * Test configuration for overriding beans during testing
 */
@TestConfiguration
public class CreditinderTestConfig {

    /**
     * Creates a test version of LoanApplicationService without sample data
     * for tests that need a clean state
     */
    @Bean
    @Primary
    @Profile("test-clean")
    public LoanApplicationService cleanLoanApplicationService() {
        // For now, just return the regular service
        // Individual tests can clear data as needed
        return new LoanApplicationService();
    }
}
