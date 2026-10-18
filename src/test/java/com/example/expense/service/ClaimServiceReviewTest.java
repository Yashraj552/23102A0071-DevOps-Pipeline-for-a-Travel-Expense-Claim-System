package com.example.expense.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import com.example.expense.repository.ClaimRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClaimServiceReviewTest {

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private ClaimService claimService;

    private Claim submittedClaim() {
        Claim claim = new Claim();
        claim.setId(1L);
        return claim;
    }

    @Test
    void approveSetsStatusApprovedAndKeepsComment() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim()));
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim result = claimService.approve(1L, "Receipt verified");

        assertEquals(ClaimStatus.APPROVED, result.getStatus());
        assertEquals("Receipt verified", result.getReviewerComment());
    }

    @Test
    void rejectWithCommentSetsStatusRejected() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim()));
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim result = claimService.reject(1L, "Amount does not match receipt");

        assertEquals(ClaimStatus.REJECTED, result.getStatus());
        assertEquals("Amount does not match receipt", result.getReviewerComment());
    }

    @Test
    void rejectWithoutCommentIsNotAllowed() {
        assertThrows(IllegalArgumentException.class, () -> claimService.reject(1L, "   "));
        verify(claimRepository, never()).save(any(Claim.class));
    }

    @Test
    void claimCannotBeDecidedTwice() {
        Claim claim = submittedClaim();
        claim.setStatus(ClaimStatus.APPROVED);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

        assertThrows(IllegalStateException.class, () -> claimService.reject(1L, "Too late"));
        verify(claimRepository, never()).save(any(Claim.class));
    }
}