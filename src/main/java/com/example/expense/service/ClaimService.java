package com.example.expense.service;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import com.example.expense.repository.ClaimRepository;
import com.example.expense.web.ClaimForm;
import java.util.List;
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

    public Optional<Claim> findById(Long id) {
        return claimRepository.findById(id);
    }
}