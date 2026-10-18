package com.example.expense.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:claimtest")
@AutoConfigureMockMvc
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void claimFormPageLoads() throws Exception {
        mockMvc.perform(get("/claims/new").with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Submit a Travel Expense Claim")));
    }

    @Test
    void submittedClaimAppearsInList() throws Exception {
        mockMvc.perform(post("/claims")
                .with(csrf())
                .with(user("employee").roles("EMPLOYEE"))
                .param("destination", "Pune Conference")
                .param("purpose", "Client meeting")
                .param("amount", "2500.00")
                .param("travelDate", "2026-10-01"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/claims"));

        mockMvc.perform(get("/claims").with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Pune Conference")));
    }
}