package com.example.expense.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import com.example.expense.repository.ClaimRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:statustest",
                              "app.upload-dir=target/test-uploads"})
@AutoConfigureMockMvc
class StatusTrackingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    private Claim saveClaim(String owner, String destination, ClaimStatus status, String comment) {
        Claim claim = new Claim();
        claim.setEmployeeName(owner);
        claim.setDestination(destination);
        claim.setPurpose("Client meeting");
        claim.setAmount(new BigDecimal("900.00"));
        claim.setTravelDate(LocalDate.of(2026, 10, 1));
        claim.setStatus(status);
        claim.setReviewerComment(comment);
        return claimRepository.save(claim);
    }

    @Test
    void employeeSeesOnlyOwnClaims() throws Exception {
        saveClaim("employee", "StatusTest-Own-City", ClaimStatus.SUBMITTED, null);
        saveClaim("employee2", "StatusTest-Other-City", ClaimStatus.SUBMITTED, null);

        mockMvc.perform(get("/claims").with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("StatusTest-Own-City")))
               .andExpect(content().string(not(containsString("StatusTest-Other-City"))));
    }

    @Test
    void myClaimsShowsStatusAndReviewerComment() throws Exception {
        saveClaim("employee", "StatusTest-Approved-City", ClaimStatus.APPROVED, "StatusTest looks good");

        mockMvc.perform(get("/claims").with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("APPROVED")))
               .andExpect(content().string(containsString("StatusTest looks good")));
    }

    @Test
    void employeeCannotOpenAnotherEmployeesClaim() throws Exception {
        Claim claim = saveClaim("employee2", "StatusTest-Private-City", ClaimStatus.SUBMITTED, null);

        mockMvc.perform(get("/claims/{id}", claim.getId()).with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isForbidden());
    }

    @Test
    void ownerCanOpenOwnClaim() throws Exception {
        Claim claim = saveClaim("employee", "StatusTest-Detail-City", ClaimStatus.SUBMITTED, null);

        mockMvc.perform(get("/claims/{id}", claim.getId()).with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("StatusTest-Detail-City")));
    }
}