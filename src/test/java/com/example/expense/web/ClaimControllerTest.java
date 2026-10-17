package com.example.expense.web;

import static org.hamcrest.Matchers.containsString;
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
        mockMvc.perform(get("/claims/new"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Submit a Travel Expense Claim")));
    }

    @Test
    void submittedClaimAppearsInList() throws Exception {
        mockMvc.perform(post("/claims")
                .param("employeeName", "Asha Rao")
                .param("destination", "Pune Conference")
                .param("purpose", "Client meeting")
                .param("amount", "2500.00")
                .param("travelDate", "2026-10-01"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/claims"));

        mockMvc.perform(get("/claims"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Pune Conference")));
    }
}