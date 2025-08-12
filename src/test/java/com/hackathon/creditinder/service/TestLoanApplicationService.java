package com.hackathon.creditinder.service;

import com.hackathon.creditinder.model.LoanApplication;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test version of LoanApplicationService that doesn't create sample data
 */
public class TestLoanApplicationService extends LoanApplicationService {
    
    private final Map<String, LoanApplication> applications = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public TestLoanApplicationService() {
        // Don't call super() to avoid creating sample data
    }
    
    @Override
    public LoanApplication submitApplication(LoanApplication application) {
        applications.put(application.getId(), application);
        return application;
    }
    
    @Override
    public List<LoanApplication> getAllApplications() {
        return new ArrayList<>(applications.values());
    }
    
    @Override
    public Optional<LoanApplication> getApplicationById(String id) {
        return Optional.ofNullable(applications.get(id));
    }
    
    @Override
    public LoanApplication getRandomApplication() {
        List<LoanApplication> allApps = getAllApplications();
        if (allApps.isEmpty()) {
            return null;
        }
        return allApps.get(random.nextInt(allApps.size()));
    }
    
    @Override
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
}
