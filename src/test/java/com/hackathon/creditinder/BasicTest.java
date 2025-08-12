package com.hackathon.creditinder;

import com.hackathon.creditinder.model.LoanApplication;
import com.hackathon.creditinder.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Basic Functionality Tests")
class BasicTest {

    @Test
    @DisplayName("Should create loan application")
    void shouldCreateLoanApplication() {
        LoanApplication app = new LoanApplication();
        app.setApplicantName("Test User");
        app.setLoanAmount(new BigDecimal("10000"));
        app.setLoanPurpose("Test");
        app.setAnnualIncome(new BigDecimal("50000"));
        app.setCreditScore(700);
        app.setEmploymentStatus("Full-time");
        
        assertNotNull(app.getId());
        assertEquals("Test User", app.getApplicantName());
        assertEquals(0, app.getTotalVotes());
    }

    @Test
    @DisplayName("Should create service and submit application")
    void shouldCreateServiceAndSubmitApplication() {
        LoanApplicationService service = new LoanApplicationService();
        
        LoanApplication app = new LoanApplication();
        app.setApplicantName("Test User");
        app.setLoanAmount(new BigDecimal("10000"));
        app.setLoanPurpose("Test");
        app.setAnnualIncome(new BigDecimal("50000"));
        app.setCreditScore(700);
        app.setEmploymentStatus("Full-time");
        
        LoanApplication submitted = service.submitApplication(app);
        
        assertNotNull(submitted);
        assertEquals(app.getId(), submitted.getId());
        assertTrue(service.getAllApplications().size() >= 1); // At least our submitted app + sample data
    }

    @Test
    @DisplayName("Should handle voting")
    void shouldHandleVoting() {
        LoanApplication app = new LoanApplication();
        
        app.addApprovalVote();
        assertEquals(1, app.getApprovalVotes());
        assertEquals(0, app.getRejectionVotes());
        assertEquals(100.0, app.getApprovalPercentage());
        
        app.addRejectionVote();
        assertEquals(1, app.getApprovalVotes());
        assertEquals(1, app.getRejectionVotes());
        assertEquals(50.0, app.getApprovalPercentage());
    }
}
