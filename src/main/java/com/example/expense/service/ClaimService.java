package com.example.expense.service;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import com.example.expense.repository.ClaimRepository;
import com.example.expense.web.ClaimForm;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Transactional
    public Claim submit(ClaimForm form, String employeeName, String receiptPath) {
        Claim claim = new Claim();
        claim.setEmployeeName(employeeName);
        claim.setDestination(form.getDestination());
        claim.setPurpose(form.getPurpose());
        claim.setAmount(form.getAmount());
        claim.setTravelDate(form.getTravelDate());
        claim.setReceiptPath(receiptPath);
        claim.setStatus(ClaimStatus.SUBMITTED);
        return claimRepository.save(claim);
    }

    public List<Claim> findAll() {
        return claimRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Claim> findPending() {
        return claimRepository.findByStatusOrderByCreatedAtDesc(ClaimStatus.SUBMITTED);
    }

    public Optional<Claim> findById(Long id) {
        return claimRepository.findById(id);
    }

    @Transactional
    public Claim approve(Long id, String comment) {
        return decide(id, ClaimStatus.APPROVED, comment);
    }

    @Transactional
    public Claim reject(Long id, String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("A comment is required when rejecting a claim");
        }
        return decide(id, ClaimStatus.REJECTED, comment);
    }

    private Claim decide(Long id, ClaimStatus newStatus, String comment) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Claim not found: " + id));
        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException("Claim has already been " + claim.getStatus().name().toLowerCase());
        }
        claim.setStatus(newStatus);
        claim.setReviewerComment(comment == null || comment.isBlank() ? null : comment.trim());
        return claimRepository.save(claim);
    }
}