package com.hackathon.creditinder.service;

import com.hackathon.creditinder.model.LoanApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanApplicationService Tests")
class LoanApplicationServiceTest {

    private LoanApplicationService service;
    private LoanApplication testApplication;

    @BeforeEach
    void setUp() {
        service = new LoanApplicationService();
        
        testApplication = new LoanApplication();
        testApplication.setApplicantName("Test User");
        testApplication.setLoanAmount(new BigDecimal("10000.00"));
        testApplication.setLoanPurpose("Test Purpose");
        testApplication.setAnnualIncome(new BigDecimal("50000.00"));
        testApplication.setCreditScore(700);
        testApplication.setEmploymentStatus("Full-time");
        testApplication.setAdditionalNotes("Test notes");
    }

    @Test
    @DisplayName("Should initialize with sample applications")
    void shouldInitializeWithSampleApplications() {
        List<LoanApplication> applications = service.getAllApplications();
        
        assertFalse(applications.isEmpty(), "Service should initialize with sample applications");
        assertEquals(4, applications.size(), "Should have 4 sample applications");
        
        // Verify sample data exists
        assertTrue(applications.stream().anyMatch(app -> "John Smith".equals(app.getApplicantName())));
        assertTrue(applications.stream().anyMatch(app -> "Sarah Johnson".equals(app.getApplicantName())));
        assertTrue(applications.stream().anyMatch(app -> "Mike Davis".equals(app.getApplicantName())));
        assertTrue(applications.stream().anyMatch(app -> "Emily Chen".equals(app.getApplicantName())));
    }

    @Test
    @DisplayName("Should submit new application successfully")
    void shouldSubmitNewApplicationSuccessfully() {
        int initialCount = service.getAllApplications().size();
        
        LoanApplication submitted = service.submitApplication(testApplication);
        
        assertNotNull(submitted);
        assertEquals(testApplication.getId(), submitted.getId());
        assertEquals(initialCount + 1, service.getAllApplications().size());
        
        // Verify the application is stored
        Optional<LoanApplication> retrieved = service.getApplicationById(testApplication.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Test User", retrieved.get().getApplicantName());
    }

    @Test
    @DisplayName("Should retrieve application by ID")
    void shouldRetrieveApplicationById() {
        service.submitApplication(testApplication);
        
        Optional<LoanApplication> retrieved = service.getApplicationById(testApplication.getId());
        
        assertTrue(retrieved.isPresent());
        assertEquals(testApplication.getId(), retrieved.get().getId());
        assertEquals("Test User", retrieved.get().getApplicantName());
    }

    @Test
    @DisplayName("Should return empty optional for non-existent ID")
    void shouldReturnEmptyOptionalForNonExistentId() {
        Optional<LoanApplication> retrieved = service.getApplicationById("non-existent-id");
        
        assertFalse(retrieved.isPresent());
    }

    @Test
    @DisplayName("Should return random application from available applications")
    void shouldReturnRandomApplicationFromAvailableApplications() {
        service.submitApplication(testApplication);
        
        LoanApplication randomApp = service.getRandomApplication();
        
        assertNotNull(randomApp);
        assertTrue(service.getAllApplications().contains(randomApp));
    }

    @Test
    @DisplayName("Should return null when no applications available for random selection")
    void shouldReturnNullWhenNoApplicationsAvailableForRandomSelection() {
        // Create a new service without sample data
        LoanApplicationService emptyService = new LoanApplicationService();
        
        LoanApplication randomApp = emptyService.getRandomApplication();
        
        assertNull(randomApp);
    }

    @Test
    @DisplayName("Should vote on application successfully - approval")
    void shouldVoteOnApplicationSuccessfullyApproval() {
        service.submitApplication(testApplication);
        String applicationId = testApplication.getId();
        
        service.voteOnApplication(applicationId, true);
        
        Optional<LoanApplication> updated = service.getApplicationById(applicationId);
        assertTrue(updated.isPresent());
        assertEquals(1, updated.get().getApprovalVotes());
        assertEquals(0, updated.get().getRejectionVotes());
        assertEquals(1, updated.get().getTotalVotes());
        assertEquals(100.0, updated.get().getApprovalPercentage());
    }

    @Test
    @DisplayName("Should vote on application successfully - rejection")
    void shouldVoteOnApplicationSuccessfullyRejection() {
        service.submitApplication(testApplication);
        String applicationId = testApplication.getId();
        
        service.voteOnApplication(applicationId, false);
        
        Optional<LoanApplication> updated = service.getApplicationById(applicationId);
        assertTrue(updated.isPresent());
        assertEquals(0, updated.get().getApprovalVotes());
        assertEquals(1, updated.get().getRejectionVotes());
        assertEquals(1, updated.get().getTotalVotes());
        assertEquals(0.0, updated.get().getApprovalPercentage());
    }

    @Test
    @DisplayName("Should handle multiple votes on same application")
    void shouldHandleMultipleVotesOnSameApplication() {
        service.submitApplication(testApplication);
        String applicationId = testApplication.getId();
        
        service.voteOnApplication(applicationId, true);
        service.voteOnApplication(applicationId, true);
        service.voteOnApplication(applicationId, false);
        
        Optional<LoanApplication> updated = service.getApplicationById(applicationId);
        assertTrue(updated.isPresent());
        assertEquals(2, updated.get().getApprovalVotes());
        assertEquals(1, updated.get().getRejectionVotes());
        assertEquals(3, updated.get().getTotalVotes());
        assertEquals(66.66666666666666, updated.get().getApprovalPercentage(), 0.01);
    }

    @Test
    @DisplayName("Should handle voting on non-existent application gracefully")
    void shouldHandleVotingOnNonExistentApplicationGracefully() {
        // This should not throw an exception
        assertDoesNotThrow(() -> service.voteOnApplication("non-existent-id", true));
        assertDoesNotThrow(() -> service.voteOnApplication("non-existent-id", false));
    }

    @Test
    @DisplayName("Should return all applications in consistent order")
    void shouldReturnAllApplicationsInConsistentOrder() {
        service.submitApplication(testApplication);
        
        List<LoanApplication> applications1 = service.getAllApplications();
        List<LoanApplication> applications2 = service.getAllApplications();
        
        assertEquals(applications1.size(), applications2.size());
        // Note: Since we're using ConcurrentHashMap, order might not be guaranteed
        // but the content should be the same
        assertTrue(applications1.containsAll(applications2));
        assertTrue(applications2.containsAll(applications1));
    }

    @Test
    @DisplayName("Should maintain thread safety with concurrent operations")
    void shouldMaintainThreadSafetyWithConcurrentOperations() throws InterruptedException {
        // Submit initial application
        service.submitApplication(testApplication);
        String applicationId = testApplication.getId();
        
        // Create multiple threads to vote concurrently
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final boolean approve = i % 2 == 0; // Alternate between approve and reject
            threads[i] = new Thread(() -> service.voteOnApplication(applicationId, approve));
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify final state
        Optional<LoanApplication> updated = service.getApplicationById(applicationId);
        assertTrue(updated.isPresent());
        assertEquals(10, updated.get().getTotalVotes());
        assertEquals(5, updated.get().getApprovalVotes());
        assertEquals(5, updated.get().getRejectionVotes());
        assertEquals(50.0, updated.get().getApprovalPercentage());
    }

    @Test
    @DisplayName("Should verify sample data integrity")
    void shouldVerifySampleDataIntegrity() {
        List<LoanApplication> applications = service.getAllApplications();
        
        // Verify each sample application has required fields
        for (LoanApplication app : applications) {
            assertNotNull(app.getId());
            assertNotNull(app.getApplicantName());
            assertNotNull(app.getLoanAmount());
            assertNotNull(app.getLoanPurpose());
            assertNotNull(app.getAnnualIncome());
            assertNotNull(app.getCreditScore());
            assertNotNull(app.getEmploymentStatus());
            assertNotNull(app.getSubmittedAt());
            
            assertTrue(app.getLoanAmount().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(app.getAnnualIncome().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(app.getCreditScore() >= 300 && app.getCreditScore() <= 850);
        }
    }
}
