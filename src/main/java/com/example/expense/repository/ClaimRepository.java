package com.example.expense.repository;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findAllByOrderByCreatedAtDesc();

    List<Claim> findByEmployeeNameOrderByCreatedAtDesc(String employeeName);

    List<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status);
}