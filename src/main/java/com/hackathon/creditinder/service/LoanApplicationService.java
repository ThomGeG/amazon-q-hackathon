package com.hackathon.creditinder.service;

import com.hackathon.creditinder.model.LoanApplication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoanApplicationService {
    
    private final Map<String, LoanApplication> applications = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public LoanApplicationService() {
        // Add some sample data for demo purposes
        createSampleApplications();
    }
    
    public LoanApplication submitApplication(LoanApplication application) {
        applications.put(application.getId(), application);
        return application;
    }
    
    public List<LoanApplication> getAllApplications() {
        return new ArrayList<>(applications.values());
    }
    
    public Optional<LoanApplication> getApplicationById(String id) {
        return Optional.ofNullable(applications.get(id));
    }
    
    public LoanApplication getRandomApplication() {
        List<LoanApplication> allApps = getAllApplications();
        if (allApps.isEmpty()) {
            return null;
        }
        return allApps.get(random.nextInt(allApps.size()));
    }
    
    public void voteOnApplication(String applicationId, boolean approve) {
        LoanApplication application = applications.get(applicationId);
        if (application != null) {
            if (approve) {
                application.addApprovalVote();
            } else {
                application.addRejectionVote();
            }
        }
    }
    
    private void createSampleApplications() {
        // Sample application 1
        LoanApplication app1 = new LoanApplication();
        app1.setApplicantName("John Smith");
        app1.setLoanAmount(new BigDecimal("25000"));
        app1.setLoanPurpose("Home Improvement");
        app1.setAnnualIncome(new BigDecimal("65000"));
        app1.setCreditScore(720);
        app1.setEmploymentStatus("Full-time");
        app1.setAdditionalNotes("Looking to renovate kitchen and bathroom. Stable employment for 5 years.");
        applications.put(app1.getId(), app1);
        
        // Sample application 2
        LoanApplication app2 = new LoanApplication();
        app2.setApplicantName("Sarah Johnson");
        app2.setLoanAmount(new BigDecimal("15000"));
        app2.setLoanPurpose("Debt Consolidation");
        app2.setAnnualIncome(new BigDecimal("48000"));
        app2.setCreditScore(680);
        app2.setEmploymentStatus("Full-time");
        app2.setAdditionalNotes("Want to consolidate credit card debt to lower interest rate.");
        applications.put(app2.getId(), app2);
        
        // Sample application 3
        LoanApplication app3 = new LoanApplication();
        app3.setApplicantName("Mike Davis");
        app3.setLoanAmount(new BigDecimal("35000"));
        app3.setLoanPurpose("Vehicle Purchase");
        app3.setAnnualIncome(new BigDecimal("72000"));
        app3.setCreditScore(750);
        app3.setEmploymentStatus("Full-time");
        app3.setAdditionalNotes("Need reliable transportation for work. Current car is 15 years old.");
        applications.put(app3.getId(), app3);
        
        // Sample application 4
        LoanApplication app4 = new LoanApplication();
        app4.setApplicantName("Emily Chen");
        app4.setLoanAmount(new BigDecimal("8000"));
        app4.setLoanPurpose("Education");
        app4.setAnnualIncome(new BigDecimal("32000"));
        app4.setCreditScore(650);
        app4.setEmploymentStatus("Part-time");
        app4.setAdditionalNotes("Pursuing certification program to advance career. Currently working part-time while studying.");
        applications.put(app4.getId(), app4);
    }
}
