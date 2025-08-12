package com.hackathon.creditinder.controller;

import com.hackathon.creditinder.model.LoanApplication;
import com.hackathon.creditinder.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CreditinderController {
    
    @Autowired
    private LoanApplicationService loanApplicationService;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/apply")
    public String showApplicationForm(Model model) {
        model.addAttribute("loanApplication", new LoanApplication());
        return "apply";
    }
    
    @PostMapping("/apply")
    public String submitApplication(@Valid @ModelAttribute LoanApplication loanApplication, 
                                  BindingResult bindingResult, 
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "apply";
        }
        
        loanApplicationService.submitApplication(loanApplication);
        redirectAttributes.addFlashAttribute("message", "Application submitted successfully!");
        return "redirect:/";
    }
    
    @GetMapping("/swipe")
    public String showSwipePage(Model model) {
        LoanApplication randomApp = loanApplicationService.getRandomApplication();
        if (randomApp == null) {
            model.addAttribute("noApplications", true);
            return "swipe";
        }
        model.addAttribute("application", randomApp);
        return "swipe";
    }
    
    @PostMapping("/vote")
    @ResponseBody
    public String vote(@RequestParam String applicationId, @RequestParam boolean approve) {
        loanApplicationService.voteOnApplication(applicationId, approve);
        return "success";
    }
    
    @GetMapping("/applications")
    public String showAllApplications(Model model) {
        model.addAttribute("applications", loanApplicationService.getAllApplications());
        return "applications";
    }
    
    @GetMapping("/application/{id}")
    public String showApplicationDetails(@PathVariable String id, Model model) {
        return loanApplicationService.getApplicationById(id)
                .map(app -> {
                    model.addAttribute("application", app);
                    return "application-details";
                })
                .orElse("redirect:/applications");
    }
}
