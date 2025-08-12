package com.hackathon.creditinder.controller;

import com.hackathon.creditinder.model.LoanApplication;
import com.hackathon.creditinder.service.LoanApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditinderController.class)
@DisplayName("CreditinderController Tests")
class CreditinderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService loanApplicationService;

    private LoanApplication testApplication;
    private List<LoanApplication> testApplications;

    @BeforeEach
    void setUp() {
        testApplication = new LoanApplication();
        testApplication.setApplicantName("John Doe");
        testApplication.setLoanAmount(new BigDecimal("25000.00"));
        testApplication.setLoanPurpose("Home Improvement");
        testApplication.setAnnualIncome(new BigDecimal("65000.00"));
        testApplication.setCreditScore(720);
        testApplication.setEmploymentStatus("Full-time");
        testApplication.setAdditionalNotes("Test notes");

        LoanApplication app2 = new LoanApplication();
        app2.setApplicantName("Jane Smith");
        app2.setLoanAmount(new BigDecimal("15000.00"));
        app2.setLoanPurpose("Debt Consolidation");
        app2.setAnnualIncome(new BigDecimal("55000.00"));
        app2.setCreditScore(680);
        app2.setEmploymentStatus("Full-time");

        testApplications = Arrays.asList(testApplication, app2);
    }

    @Test
    @DisplayName("Should display home page")
    void shouldDisplayHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Should display application form")
    void shouldDisplayApplicationForm() throws Exception {
        mockMvc.perform(get("/apply"))
                .andExpect(status().isOk())
                .andExpect(view().name("apply"))
                .andExpect(model().attributeExists("loanApplication"));
    }

    @Test
    @DisplayName("Should submit valid application successfully")
    void shouldSubmitValidApplicationSuccessfully() throws Exception {
        when(loanApplicationService.submitApplication(any(LoanApplication.class)))
                .thenReturn(testApplication);

        mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicantName", "John Doe")
                .param("loanAmount", "25000.00")
                .param("loanPurpose", "Home Improvement")
                .param("annualIncome", "65000.00")
                .param("creditScore", "720")
                .param("employmentStatus", "Full-time")
                .param("additionalNotes", "Test notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("message"));

        verify(loanApplicationService, times(1)).submitApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Should return to form with validation errors for invalid application")
    void shouldReturnToFormWithValidationErrorsForInvalidApplication() throws Exception {
        mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicantName", "") // Invalid: blank name
                .param("loanAmount", "-1000") // Invalid: negative amount
                .param("loanPurpose", "Home Improvement")
                .param("annualIncome", "65000.00")
                .param("creditScore", "720")
                .param("employmentStatus", "Full-time"))
                .andExpect(status().isOk())
                .andExpect(view().name("apply"))
                .andExpect(model().hasErrors());

        verify(loanApplicationService, never()).submitApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Should display swipe page with application")
    void shouldDisplaySwipePageWithApplication() throws Exception {
        when(loanApplicationService.getRandomApplication()).thenReturn(testApplication);

        mockMvc.perform(get("/swipe"))
                .andExpect(status().isOk())
                .andExpect(view().name("swipe"))
                .andExpect(model().attributeExists("application"))
                .andExpect(model().attribute("application", testApplication));
    }

    @Test
    @DisplayName("Should display swipe page with no applications message")
    void shouldDisplaySwipePageWithNoApplicationsMessage() throws Exception {
        when(loanApplicationService.getRandomApplication()).thenReturn(null);

        mockMvc.perform(get("/swipe"))
                .andExpect(status().isOk())
                .andExpect(view().name("swipe"))
                .andExpect(model().attributeExists("noApplications"))
                .andExpect(model().attribute("noApplications", true));
    }

    @Test
    @DisplayName("Should process approval vote successfully")
    void shouldProcessApprovalVoteSuccessfully() throws Exception {
        doNothing().when(loanApplicationService).voteOnApplication("test-id", true);

        mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationId", "test-id")
                .param("approve", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        verify(loanApplicationService, times(1)).voteOnApplication("test-id", true);
    }

    @Test
    @DisplayName("Should process rejection vote successfully")
    void shouldProcessRejectionVoteSuccessfully() throws Exception {
        doNothing().when(loanApplicationService).voteOnApplication("test-id", false);

        mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationId", "test-id")
                .param("approve", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        verify(loanApplicationService, times(1)).voteOnApplication("test-id", false);
    }

    @Test
    @DisplayName("Should handle voting error gracefully")
    void shouldHandleVotingErrorGracefully() throws Exception {
        doThrow(new RuntimeException("Database error"))
                .when(loanApplicationService).voteOnApplication("test-id", true);

        mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationId", "test-id")
                .param("approve", "true"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("error"));

        verify(loanApplicationService, times(1)).voteOnApplication("test-id", true);
    }

    @Test
    @DisplayName("Should display all applications")
    void shouldDisplayAllApplications() throws Exception {
        when(loanApplicationService.getAllApplications()).thenReturn(testApplications);

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("applications"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attribute("applications", testApplications));
    }

    @Test
    @DisplayName("Should display application details for existing application")
    void shouldDisplayApplicationDetailsForExistingApplication() throws Exception {
        when(loanApplicationService.getApplicationById("test-id"))
                .thenReturn(Optional.of(testApplication));

        mockMvc.perform(get("/application/test-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attributeExists("application"))
                .andExpect(model().attribute("application", testApplication));
    }

    @Test
    @DisplayName("Should redirect to applications list for non-existent application")
    void shouldRedirectToApplicationsListForNonExistentApplication() throws Exception {
        when(loanApplicationService.getApplicationById("non-existent-id"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/application/non-existent-id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));
    }

    @Test
    @DisplayName("Should handle missing vote parameters")
    void shouldHandleMissingVoteParameters() throws Exception {
        mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationId", "test-id"))
                // Missing 'approve' parameter
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid vote parameter")
    void shouldHandleInvalidVoteParameter() throws Exception {
        mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationId", "test-id")
                .param("approve", "invalid-boolean"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate required fields in application submission")
    void shouldValidateRequiredFieldsInApplicationSubmission() throws Exception {
        mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicantName", "")
                .param("loanAmount", "")
                .param("loanPurpose", "")
                .param("annualIncome", "")
                .param("creditScore", "")
                .param("employmentStatus", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("apply"))
                .andExpect(model().hasErrors()); // Just check that there are errors

        verify(loanApplicationService, never()).submitApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Should accept valid credit score range")
    void shouldAcceptValidCreditScoreRange() throws Exception {
        when(loanApplicationService.submitApplication(any(LoanApplication.class)))
                .thenReturn(testApplication);

        // Test minimum valid credit score
        mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicantName", "John Doe")
                .param("loanAmount", "25000.00")
                .param("loanPurpose", "Home Improvement")
                .param("annualIncome", "65000.00")
                .param("creditScore", "300") // Minimum valid score
                .param("employmentStatus", "Full-time"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Test maximum valid credit score
        mockMvc.perform(post("/apply")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicantName", "John Doe")
                .param("loanAmount", "25000.00")
                .param("loanPurpose", "Home Improvement")
                .param("annualIncome", "65000.00")
                .param("creditScore", "850") // Maximum valid score
                .param("employmentStatus", "Full-time"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(loanApplicationService, times(2)).submitApplication(any(LoanApplication.class));
    }
}
