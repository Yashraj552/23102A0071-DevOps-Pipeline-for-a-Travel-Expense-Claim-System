package com.example.expense.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import com.example.expense.repository.ClaimRepository;
import com.example.expense.web.ClaimForm;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private ClaimService claimService;

    @Test
    void submitSavesClaimWithOwnerAndStatusSubmitted() {
        ClaimForm form = new ClaimForm();
        form.setDestination("Pune");
        form.setPurpose("Client meeting");
        form.setAmount(new BigDecimal("2500.00"));
        form.setTravelDate(LocalDate.of(2026, 10, 1));
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim saved = claimService.submit(form, "employee", "receipt.pdf");

        assertEquals(ClaimStatus.SUBMITTED, saved.getStatus());
        assertEquals("employee", saved.getEmployeeName());
        assertEquals("receipt.pdf", saved.getReceiptPath());
        assertEquals("Pune", saved.getDestination());
        verify(claimRepository).save(any(Claim.class));
    }
}