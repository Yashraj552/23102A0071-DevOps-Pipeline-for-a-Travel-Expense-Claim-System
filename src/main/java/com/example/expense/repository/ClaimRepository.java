package com.example.expense.repository;

import com.example.expense.model.Claim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findAllByOrderByCreatedAtDesc();
}