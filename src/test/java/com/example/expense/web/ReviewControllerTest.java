package com.example.expense.web;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import com.example.expense.repository.ClaimRepository;
import com.example.expense.service.ReceiptStorageService;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:reviewtest",
                              "app.upload-dir=target/test-uploads"})
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ReceiptStorageService receiptStorageService;

    private static RequestPostProcessor reviewer() {
        return user("reviewer").roles("REVIEWER");
    }

    private Claim saveClaim(String owner, String destination, String receiptPath) {
        Claim claim = new Claim();
        claim.setEmployeeName(owner);
        claim.setDestination(destination);
        claim.setPurpose("Client meeting");
        claim.setAmount(new BigDecimal("1500.00"));
        claim.setTravelDate(LocalDate.of(2026, 10, 1));
        claim.setReceiptPath(receiptPath);
        return claimRepository.save(claim);
    }

    @Test
    void reviewerSeesPendingClaims() throws Exception {
        saveClaim("employee", "ReviewTest-Pending-City", null);
        mockMvc.perform(get("/review").with(reviewer()))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("ReviewTest-Pending-City")));
    }

    @Test
    void approveChangesStatusToApproved() throws Exception {
        Claim claim = saveClaim("employee", "ReviewTest-Approve-City", null);
        mockMvc.perform(post("/review/{id}/approve", claim.getId())
                .with(csrf()).with(reviewer()).param("comment", "Receipt checked"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/review"));

        Claim saved = claimRepository.findById(claim.getId()).orElseThrow();
        assertEquals(ClaimStatus.APPROVED, saved.getStatus());
        assertEquals("Receipt checked", saved.getReviewerComment());
    }

    @Test
    void rejectWithoutCommentKeepsClaimSubmitted() throws Exception {
        Claim claim = saveClaim("employee", "ReviewTest-NoComment-City", null);
        mockMvc.perform(post("/review/{id}/reject", claim.getId())
                .with(csrf()).with(reviewer()).param("comment", "  "))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/review/" + claim.getId()));

        Claim saved = claimRepository.findById(claim.getId()).orElseThrow();
        assertEquals(ClaimStatus.SUBMITTED, saved.getStatus());
    }

    @Test
    void rejectWithCommentSavesStatusAndComment() throws Exception {
        Claim claim = saveClaim("employee", "ReviewTest-Reject-City", null);
        mockMvc.perform(post("/review/{id}/reject", claim.getId())
                .with(csrf()).with(reviewer()).param("comment", "Amount does not match receipt"))
               .andExpect(redirectedUrl("/review"));

        Claim saved = claimRepository.findById(claim.getId()).orElseThrow();
        assertEquals(ClaimStatus.REJECTED, saved.getStatus());
        assertEquals("Amount does not match receipt", saved.getReviewerComment());
    }

    @Test
    void employeeCannotOpenReviewList() throws Exception {
        mockMvc.perform(get("/review").with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isForbidden());
    }

    @Test
    void reviewerAndOwnerCanOpenReceiptButOtherEmployeeCannot() throws Exception {
        String stored = receiptStorageService.store(
                new MockMultipartFile("receipt", "r.pdf", "application/pdf", "pdf-content".getBytes()));
        Claim claim = saveClaim("employee", "ReviewTest-Receipt-City", stored);

        mockMvc.perform(get("/receipts/{id}", claim.getId()).with(reviewer()))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/pdf"));
        mockMvc.perform(get("/receipts/{id}", claim.getId()).with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk());
        mockMvc.perform(get("/receipts/{id}", claim.getId()).with(user("employee2").roles("EMPLOYEE")))
               .andExpect(status().isForbidden());
    }
}