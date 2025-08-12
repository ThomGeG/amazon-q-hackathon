package com.hackathon.creditinder;

import com.hackathon.creditinder.model.LoanApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating test data and common test operations
 */
public class TestUtils {

    /**
     * Creates a valid loan application for testing
     */
    public static LoanApplication createValidLoanApplication() {
        LoanApplication app = new LoanApplication();
        app.setApplicantName("Test User");
        app.setLoanAmount(new BigDecimal("25000.00"));
        app.setLoanPurpose("Home Improvement");
        app.setAnnualIncome(new BigDecimal("65000.00"));
        app.setCreditScore(720);
        app.setEmploymentStatus("Full-time");
        app.setAdditionalNotes("Test application for unit testing");
        return app;
    }

    /**
     * Creates a loan application with specific parameters
     */
    public static LoanApplication createLoanApplication(String name, BigDecimal amount, 
            String purpose, BigDecimal income, Integer creditScore, String employment) {
        LoanApplication app = new LoanApplication();
        app.setApplicantName(name);
        app.setLoanAmount(amount);
        app.setLoanPurpose(purpose);
        app.setAnnualIncome(income);
        app.setCreditScore(creditScore);
        app.setEmploymentStatus(employment);
        return app;
    }

    /**
     * Creates a loan application with invalid data for validation testing
     */
    public static LoanApplication createInvalidLoanApplication() {
        LoanApplication app = new LoanApplication();
        app.setApplicantName(""); // Invalid: blank
        app.setLoanAmount(new BigDecimal("-1000")); // Invalid: negative
        app.setLoanPurpose(""); // Invalid: blank
        app.setAnnualIncome(new BigDecimal("-5000")); // Invalid: negative
        app.setCreditScore(null); // Invalid: null
        app.setEmploymentStatus(""); // Invalid: blank
        return app;
    }

    /**
     * Creates a list of test loan applications
     */
    public static List<LoanApplication> createTestApplications(int count) {
        List<LoanApplication> applications = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            LoanApplication app = new LoanApplication();
            app.setApplicantName("Test User " + (i + 1));
            app.setLoanAmount(new BigDecimal(String.valueOf(10000 + (i * 5000))));
            app.setLoanPurpose("Test Purpose " + (i + 1));
            app.setAnnualIncome(new BigDecimal(String.valueOf(50000 + (i * 10000))));
            app.setCreditScore(650 + (i * 20));
            app.setEmploymentStatus("Full-time");
            app.setAdditionalNotes("Test application " + (i + 1));
            
            applications.add(app);
        }
        
        return applications;
    }

    /**
     * Creates a loan application with votes for testing voting functionality
     */
    public static LoanApplication createApplicationWithVotes(int approvals, int rejections) {
        LoanApplication app = createValidLoanApplication();
        
        // Add approval votes
        for (int i = 0; i < approvals; i++) {
            app.addApprovalVote();
        }
        
        // Add rejection votes
        for (int i = 0; i < rejections; i++) {
            app.addRejectionVote();
        }
        
        return app;
    }

    /**
     * Creates a high-risk loan application (low credit score, high loan amount)
     */
    public static LoanApplication createHighRiskApplication() {
        LoanApplication app = new LoanApplication();
        app.setApplicantName("High Risk User");
        app.setLoanAmount(new BigDecimal("50000.00")); // High amount
        app.setLoanPurpose("Debt Consolidation");
        app.setAnnualIncome(new BigDecimal("35000.00")); // Lower income
        app.setCreditScore(580); // Low credit score
        app.setEmploymentStatus("Part-time");
        app.setAdditionalNotes("High risk test application");
        return app;
    }

    /**
     * Creates a low-risk loan application (high credit score, reasonable loan amount)
     */
    public static LoanApplication createLowRiskApplication() {
        LoanApplication app = new LoanApplication();
        app.setApplicantName("Low Risk User");
        app.setLoanAmount(new BigDecimal("15000.00")); // Reasonable amount
        app.setLoanPurpose("Home Improvement");
        app.setAnnualIncome(new BigDecimal("85000.00")); // High income
        app.setCreditScore(780); // High credit score
        app.setEmploymentStatus("Full-time");
        app.setAdditionalNotes("Low risk test application");
        return app;
    }

    /**
     * Validates that a loan application has all required fields set
     */
    public static boolean isValidApplication(LoanApplication app) {
        return app != null &&
               app.getApplicantName() != null && !app.getApplicantName().trim().isEmpty() &&
               app.getLoanAmount() != null && app.getLoanAmount().compareTo(BigDecimal.ZERO) > 0 &&
               app.getLoanPurpose() != null && !app.getLoanPurpose().trim().isEmpty() &&
               app.getAnnualIncome() != null && app.getAnnualIncome().compareTo(BigDecimal.ZERO) > 0 &&
               app.getCreditScore() != null && app.getCreditScore() >= 300 && app.getCreditScore() <= 850 &&
               app.getEmploymentStatus() != null && !app.getEmploymentStatus().trim().isEmpty();
    }

    /**
     * Calculates expected approval percentage for testing
     */
    public static double calculateExpectedApprovalPercentage(int approvals, int rejections) {
        int total = approvals + rejections;
        if (total == 0) return 0.0;
        return (double) approvals / total * 100.0;
    }
}
