package com.example.expense.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:claimtest",
                              "app.upload-dir=target/test-uploads"})
@AutoConfigureMockMvc
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static String yesterday() {
        return LocalDate.now().minusDays(1).toString();
    }

    private static MockMultipartFile pdf() {
        return new MockMultipartFile("receipt", "receipt.pdf", "application/pdf", "sample receipt".getBytes());
    }

    private MockHttpServletRequestBuilder claimRequest(String amount, String date, MockMultipartFile receipt) {
        return multipart("/claims")
                .file(receipt)
                .param("destination", "Pune Conference")
                .param("purpose", "Client meeting")
                .param("amount", amount)
                .param("travelDate", date)
                .with(csrf())
                .with(user("employee").roles("EMPLOYEE"));
    }

    @Test
    void claimFormPageLoads() throws Exception {
        mockMvc.perform(get("/claims/new").with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Submit a Travel Expense Claim")));
    }

    @Test
    void validClaimIsSavedAndListed() throws Exception {
        mockMvc.perform(claimRequest("2500.00", yesterday(), pdf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/claims"));

        mockMvc.perform(get("/claims").with(user("employee").roles("EMPLOYEE")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Pune Conference")));
    }

    @Test
    void negativeAmountIsRejected() throws Exception {
        mockMvc.perform(claimRequest("-5", yesterday(), pdf()))
               .andExpect(status().isOk())
               .andExpect(view().name("claim-form"))
               .andExpect(model().attributeHasFieldErrors("claimForm", "amount"));
    }

    @Test
    void zeroAmountIsRejected() throws Exception {
        mockMvc.perform(claimRequest("0", yesterday(), pdf()))
               .andExpect(view().name("claim-form"))
               .andExpect(model().attributeHasFieldErrors("claimForm", "amount"));
    }

    @Test
    void futureTravelDateIsRejected() throws Exception {
        mockMvc.perform(claimRequest("100.00", LocalDate.now().plusDays(1).toString(), pdf()))
               .andExpect(view().name("claim-form"))
               .andExpect(model().attributeHasFieldErrors("claimForm", "travelDate"));
    }

    @Test
    void missingReceiptIsRejected() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("receipt", "", "application/octet-stream", new byte[0]);
        mockMvc.perform(claimRequest("100.00", yesterday(), empty))
               .andExpect(view().name("claim-form"))
               .andExpect(model().attributeExists("receiptError"));
    }

    @Test
    void wrongReceiptTypeIsRejected() throws Exception {
        MockMultipartFile txt = new MockMultipartFile("receipt", "notes.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(claimRequest("100.00", yesterday(), txt))
               .andExpect(view().name("claim-form"))
               .andExpect(model().attributeExists("receiptError"));
    }

    @Test
    void oversizedReceiptIsRejected() throws Exception {
        MockMultipartFile big = new MockMultipartFile("receipt", "big.pdf", "application/pdf",
                new byte[2 * 1024 * 1024 + 1]);
        mockMvc.perform(claimRequest("100.00", yesterday(), big))
               .andExpect(view().name("claim-form"))
               .andExpect(model().attributeExists("receiptError"));
    }
}