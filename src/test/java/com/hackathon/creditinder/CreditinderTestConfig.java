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
        // Create a service and then clear the sample data
        LoanApplicationService service = new LoanApplicationService();
        // Clear all applications to start with a clean state
        service.getAllApplications().clear();
        return service;
    }
}
