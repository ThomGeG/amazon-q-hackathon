package com.hackathon.creditinder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanApplication Model Tests")
class LoanApplicationTest {

    private Validator validator;
    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        loanApplication = new LoanApplication();
        loanApplication.setApplicantName("John Doe");
        loanApplication.setLoanAmount(new BigDecimal("25000.00"));
        loanApplication.setLoanPurpose("Home Improvement");
        loanApplication.setAnnualIncome(new BigDecimal("65000.00"));
        loanApplication.setCreditScore(720);
        loanApplication.setEmploymentStatus("Full-time");
        loanApplication.setAdditionalNotes("Test notes");
    }

    @Test
    @DisplayName("Should create valid loan application with all required fields")
    void shouldCreateValidLoanApplication() {
        Set<ConstraintViolation<LoanApplication>> violations = validator.validate(loanApplication);
        assertTrue(violations.isEmpty(), "Valid loan application should have no validation errors");
    }

    @Test
    @DisplayName("Should generate unique ID on creation")
    void shouldGenerateUniqueId() {
        LoanApplication app1 = new LoanApplication();
        LoanApplication app2 = new LoanApplication();
        
        assertNotNull(app1.getId());
        assertNotNull(app2.getId());
        assertNotEquals(app1.getId(), app2.getId());
    }

    @Test
    @DisplayName("Should set submission timestamp on creation")
    void shouldSetSubmissionTimestamp() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        LoanApplication app = new LoanApplication();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        
        assertNotNull(app.getSubmittedAt());
        assertTrue(app.getSubmittedAt().isAfter(before));
        assertTrue(app.getSubmittedAt().isBefore(after));
    }

    @Test
    @DisplayName("Should initialize vote counts to zero")
    void shouldInitializeVoteCountsToZero() {
        LoanApplication app = new LoanApplication();
        
        assertEquals(0, app.getApprovalVotes());
        assertEquals(0, app.getRejectionVotes());
        assertEquals(0, app.getTotalVotes());
        assertEquals(0.0, app.getApprovalPercentage());
    }

    @Test
    @DisplayName("Should fail validation when applicant name is blank")
    void shouldFailValidationWhenApplicantNameIsBlank() {
        loanApplication.setApplicantName("");
        
        Set<ConstraintViolation<LoanApplication>> violations = validator.validate(loanApplication);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("applicantName")));
    }

    @Test
    @DisplayName("Should fail validation when loan amount is null")
    void shouldFailValidationWhenLoanAmountIsNull() {
        loanApplication.setLoanAmount(null);
        
        Set<ConstraintViolation<LoanApplication>> violations = validator.validate(loanApplication);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loanAmount")));
    }

    @Test
    @DisplayName("Should fail validation when loan amount is negative")
    void shouldFailValidationWhenLoanAmountIsNegative() {
        loanApplication.setLoanAmount(new BigDecimal("-1000"));
        
        Set<ConstraintViolation<LoanApplication>> violations = validator.validate(loanApplication);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loanAmount")));
    }

    @Test
    @DisplayName("Should fail validation when annual income is null")
    void shouldFailValidationWhenAnnualIncomeIsNull() {
        loanApplication.setAnnualIncome(null);
        
        Set<ConstraintViolation<LoanApplication>> violations = validator.validate(loanApplication);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("annualIncome")));
    }

    @Test
    @DisplayName("Should fail validation when credit score is null")
    void shouldFailValidationWhenCreditScoreIsNull() {
        loanApplication.setCreditScore(null);
        
        Set<ConstraintViolation<LoanApplication>> violations = validator.validate(loanApplication);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("creditScore")));
    }

    @Test
    @DisplayName("Should correctly add approval votes")
    void shouldCorrectlyAddApprovalVotes() {
        loanApplication.addApprovalVote();
        loanApplication.addApprovalVote();
        
        assertEquals(2, loanApplication.getApprovalVotes());
        assertEquals(0, loanApplication.getRejectionVotes());
        assertEquals(2, loanApplication.getTotalVotes());
        assertEquals(100.0, loanApplication.getApprovalPercentage());
    }

    @Test
    @DisplayName("Should correctly add rejection votes")
    void shouldCorrectlyAddRejectionVotes() {
        loanApplication.addRejectionVote();
        loanApplication.addRejectionVote();
        
        assertEquals(0, loanApplication.getApprovalVotes());
        assertEquals(2, loanApplication.getRejectionVotes());
        assertEquals(2, loanApplication.getTotalVotes());
        assertEquals(0.0, loanApplication.getApprovalPercentage());
    }

    @Test
    @DisplayName("Should correctly calculate approval percentage with mixed votes")
    void shouldCorrectlyCalculateApprovalPercentageWithMixedVotes() {
        loanApplication.addApprovalVote();
        loanApplication.addApprovalVote();
        loanApplication.addApprovalVote();
        loanApplication.addRejectionVote();
        
        assertEquals(3, loanApplication.getApprovalVotes());
        assertEquals(1, loanApplication.getRejectionVotes());
        assertEquals(4, loanApplication.getTotalVotes());
        assertEquals(75.0, loanApplication.getApprovalPercentage());
    }

    @Test
    @DisplayName("Should return zero approval percentage when no votes")
    void shouldReturnZeroApprovalPercentageWhenNoVotes() {
        assertEquals(0.0, loanApplication.getApprovalPercentage());
    }

    @Test
    @DisplayName("Should handle additional notes as optional field")
    void shouldHandleAdditionalNotesAsOptionalField() {
        loanApplication.setAdditionalNotes(null);
        
        Set<ConstraintViolation<LoanApplication>> violations = validator.validate(loanApplication);
        assertTrue(violations.isEmpty(), "Additional notes should be optional");
        
        loanApplication.setAdditionalNotes("");
        violations = validator.validate(loanApplication);
        assertTrue(violations.isEmpty(), "Empty additional notes should be valid");
    }
}
