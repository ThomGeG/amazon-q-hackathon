package com.hackathon.creditinder;

import com.hackathon.creditinder.model.LoanApplication;
import com.hackathon.creditinder.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Creditinder Application Integration Tests")
class CreditinderApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Test
    @DisplayName("Should load Spring context successfully")
    void shouldLoadSpringContextSuccessfully() {
        // This test verifies that the Spring context loads without errors
        assertNotNull(restTemplate);
        assertNotNull(loanApplicationService);
    }

    @Test
    @DisplayName("Should serve home page successfully")
    void shouldServeHomePageSuccessfully() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Welcome to Creditinder"));
        assertTrue(response.getBody().contains("Apply for a Loan"));
        assertTrue(response.getBody().contains("Vote on Applications"));
    }

    @Test
    @DisplayName("Should serve application form page successfully")
    void shouldServeApplicationFormPageSuccessfully() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/apply", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Loan Application"));
        assertTrue(response.getBody().contains("Full Name"));
        assertTrue(response.getBody().contains("Loan Amount"));
        assertTrue(response.getBody().contains("Credit Score"));
    }

    @Test
    @DisplayName("Should serve swipe page successfully")
    void shouldServeSwipePageSuccessfully() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/swipe", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Swipe to Vote") || 
                  response.getBody().contains("No Applications Available"));
    }

    @Test
    @DisplayName("Should serve applications list page successfully")
    void shouldServeApplicationsListPageSuccessfully() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/applications", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("All Loan Applications"));
    }

    @Test
    @DisplayName("Should submit loan application successfully via HTTP")
    void shouldSubmitLoanApplicationSuccessfullyViaHttp() {
        int initialCount = loanApplicationService.getAllApplications().size();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("applicantName", "Integration Test User");
        formData.add("loanAmount", "30000.00");
        formData.add("loanPurpose", "Home Improvement");
        formData.add("annualIncome", "70000.00");
        formData.add("creditScore", "750");
        formData.add("employmentStatus", "Full-time");
        formData.add("additionalNotes", "Integration test application");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/apply", formData, String.class);

        // Should redirect to home page after successful submission
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertTrue(response.getHeaders().getLocation().toString().contains("/"));

        // Verify application was actually saved
        assertEquals(initialCount + 1, loanApplicationService.getAllApplications().size());
        
        // Find the submitted application
        List<LoanApplication> applications = loanApplicationService.getAllApplications();
        LoanApplication submitted = applications.stream()
                .filter(app -> "Integration Test User".equals(app.getApplicantName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(submitted);
        assertEquals(0, new BigDecimal("30000.00").compareTo(submitted.getLoanAmount()));
        assertEquals("Home Improvement", submitted.getLoanPurpose());
        assertEquals(0, new BigDecimal("70000.00").compareTo(submitted.getAnnualIncome()));
        assertEquals(750, submitted.getCreditScore());
        assertEquals("Full-time", submitted.getEmploymentStatus());
        assertEquals("Integration test application", submitted.getAdditionalNotes());
    }

    @Test
    @DisplayName("Should handle voting via HTTP successfully")
    void shouldHandleVotingViaHttpSuccessfully() {
        // First, submit an application to vote on
        LoanApplication testApp = new LoanApplication();
        testApp.setApplicantName("Vote Test User");
        testApp.setLoanAmount(new BigDecimal("20000.00"));
        testApp.setLoanPurpose("Vehicle Purchase");
        testApp.setAnnualIncome(new BigDecimal("60000.00"));
        testApp.setCreditScore(700);
        testApp.setEmploymentStatus("Full-time");
        
        LoanApplication submitted = loanApplicationService.submitApplication(testApp);
        String applicationId = submitted.getId();

        // Vote on the application
        MultiValueMap<String, String> voteData = new LinkedMultiValueMap<>();
        voteData.add("applicationId", applicationId);
        voteData.add("approve", "true");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/vote", voteData, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody());

        // Verify the vote was recorded
        LoanApplication updated = loanApplicationService.getApplicationById(applicationId).orElse(null);
        assertNotNull(updated);
        assertEquals(1, updated.getApprovalVotes());
        assertEquals(0, updated.getRejectionVotes());
        assertEquals(100.0, updated.getApprovalPercentage());
    }

    @Test
    @DisplayName("Should handle form validation errors properly")
    void shouldHandleFormValidationErrorsProperly() {
        MultiValueMap<String, String> invalidFormData = new LinkedMultiValueMap<>();
        invalidFormData.add("applicantName", ""); // Invalid: blank
        invalidFormData.add("loanAmount", "-1000"); // Invalid: negative
        invalidFormData.add("loanPurpose", "");
        invalidFormData.add("annualIncome", "");
        invalidFormData.add("creditScore", "");
        invalidFormData.add("employmentStatus", "");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/apply", invalidFormData, String.class);

        // Should return to form with validation errors
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Loan Application"));
        // Should contain validation error styling
        assertTrue(response.getBody().contains("is-invalid") || 
                  response.getBody().contains("error"));
    }

    @Test
    @DisplayName("Should serve application details page for existing application")
    void shouldServeApplicationDetailsPageForExistingApplication() {
        // Get a sample application ID
        List<LoanApplication> applications = loanApplicationService.getAllApplications();
        assertFalse(applications.isEmpty(), "Should have sample applications");
        
        String applicationId = applications.get(0).getId();

        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/application/" + applicationId, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Loan Application Details"));
        assertTrue(response.getBody().contains("Voting Results"));
        assertTrue(response.getBody().contains("Risk Assessment"));
    }

    @Test
    @DisplayName("Should redirect for non-existent application details")
    void shouldRedirectForNonExistentApplicationDetails() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/application/non-existent-id", String.class);

        // Should get a successful response (TestRestTemplate follows redirects by default)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Should NOT contain application details content, but should contain applications list content
        assertFalse(response.getBody().contains("Loan Application Details"));
        assertTrue(response.getBody().contains("All Loan Applications") || 
                  response.getBody().contains("No Applications Yet"));
    }

    @Test
    @DisplayName("Should handle redirect behavior correctly")
    void shouldHandleRedirectBehaviorCorrectly() {
        // This test verifies that our redirect logic works
        // by checking that a valid ID shows details and invalid ID shows list
        
        // Get a valid application ID
        List<LoanApplication> applications = loanApplicationService.getAllApplications();
        assertFalse(applications.isEmpty(), "Should have sample applications");
        
        String validId = applications.get(0).getId();
        
        // Test valid ID - should show application details
        ResponseEntity<String> validResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/application/" + validId, String.class);
        
        assertEquals(HttpStatus.OK, validResponse.getStatusCode());
        assertTrue(validResponse.getBody().contains("Loan Application Details"));
        
        // Test invalid ID - should redirect to applications list
        ResponseEntity<String> invalidResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/application/invalid-id-12345", String.class);
        
        assertEquals(HttpStatus.OK, invalidResponse.getStatusCode());
        assertFalse(invalidResponse.getBody().contains("Loan Application Details"));
        assertTrue(invalidResponse.getBody().contains("All Loan Applications"));
    }

    @Test
    @DisplayName("Should initialize with sample data")
    void shouldInitializeWithSampleData() {
        List<LoanApplication> applications = loanApplicationService.getAllApplications();
        
        assertEquals(4, applications.size(), "Should have 4 sample applications");
        
        // Verify sample applications exist
        assertTrue(applications.stream().anyMatch(app -> 
            "John Smith".equals(app.getApplicantName()) && 
            "Home Improvement".equals(app.getLoanPurpose())));
        
        assertTrue(applications.stream().anyMatch(app -> 
            "Sarah Johnson".equals(app.getApplicantName()) && 
            "Debt Consolidation".equals(app.getLoanPurpose())));
        
        assertTrue(applications.stream().anyMatch(app -> 
            "Mike Davis".equals(app.getApplicantName()) && 
            "Vehicle Purchase".equals(app.getLoanPurpose())));
        
        assertTrue(applications.stream().anyMatch(app -> 
            "Emily Chen".equals(app.getApplicantName()) && 
            "Education".equals(app.getLoanPurpose())));
    }

    @Test
    @DisplayName("Should handle concurrent voting correctly")
    void shouldHandleConcurrentVotingCorrectly() throws InterruptedException {
        // Submit a test application
        LoanApplication testApp = new LoanApplication();
        testApp.setApplicantName("Concurrent Test User");
        testApp.setLoanAmount(new BigDecimal("25000.00"));
        testApp.setLoanPurpose("Test Purpose");
        testApp.setAnnualIncome(new BigDecimal("65000.00"));
        testApp.setCreditScore(720);
        testApp.setEmploymentStatus("Full-time");
        
        LoanApplication submitted = loanApplicationService.submitApplication(testApp);
        String applicationId = submitted.getId();

        // Create multiple threads to vote concurrently
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final boolean approve = i % 2 == 0;
            threads[i] = new Thread(() -> {
                MultiValueMap<String, String> voteData = new LinkedMultiValueMap<>();
                voteData.add("applicationId", applicationId);
                voteData.add("approve", String.valueOf(approve));

                ResponseEntity<String> response = restTemplate.postForEntity(
                        "http://localhost:" + port + "/vote", voteData, String.class);
                
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody());
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify final vote counts
        LoanApplication updated = loanApplicationService.getApplicationById(applicationId).orElse(null);
        assertNotNull(updated);
        assertEquals(5, updated.getTotalVotes());
        assertEquals(3, updated.getApprovalVotes()); // 0, 2, 4 are even (approve)
        assertEquals(2, updated.getRejectionVotes()); // 1, 3 are odd (reject)
        assertEquals(60.0, updated.getApprovalPercentage());
    }
}
