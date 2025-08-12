package com.hackathon.creditinder.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class LoanApplication {
    
    private String id;
    
    @NotBlank(message = "Applicant name is required")
    private String applicantName;
    
    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    private BigDecimal loanAmount;
    
    @NotBlank(message = "Loan purpose is required")
    private String loanPurpose;
    
    @NotNull(message = "Annual income is required")
    @Positive(message = "Annual income must be positive")
    private BigDecimal annualIncome;
    
    @NotNull(message = "Credit score is required")
    private Integer creditScore;
    
    @NotBlank(message = "Employment status is required")
    private String employmentStatus;
    
    private String additionalNotes;
    
    private LocalDateTime submittedAt;
    
    private int approvalVotes;
    private int rejectionVotes;
    
    public LoanApplication() {
        this.id = UUID.randomUUID().toString();
        this.submittedAt = LocalDateTime.now();
        this.approvalVotes = 0;
        this.rejectionVotes = 0;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getApplicantName() {
        return applicantName;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public BigDecimal getLoanAmount() {
        return loanAmount;
    }
    
    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }
    
    public String getLoanPurpose() {
        return loanPurpose;
    }
    
    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }
    
    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }
    
    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }
    
    public Integer getCreditScore() {
        return creditScore;
    }
    
    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }
    
    public String getEmploymentStatus() {
        return employmentStatus;
    }
    
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public int getApprovalVotes() {
        return approvalVotes;
    }
    
    public void setApprovalVotes(int approvalVotes) {
        this.approvalVotes = approvalVotes;
    }
    
    public int getRejectionVotes() {
        return rejectionVotes;
    }
    
    public void setRejectionVotes(int rejectionVotes) {
        this.rejectionVotes = rejectionVotes;
    }
    
    public int getTotalVotes() {
        return approvalVotes + rejectionVotes;
    }
    
    public double getApprovalPercentage() {
        if (getTotalVotes() == 0) return 0.0;
        return (double) approvalVotes / getTotalVotes() * 100;
    }
    
    public void addApprovalVote() {
        this.approvalVotes++;
    }
    
    public void addRejectionVote() {
        this.rejectionVotes++;
    }
}
