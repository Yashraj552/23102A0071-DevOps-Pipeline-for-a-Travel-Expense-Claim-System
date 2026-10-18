package com.example.expense.web;

import com.example.expense.model.Claim;
import com.example.expense.service.ClaimService;
import com.example.expense.service.ReceiptStorageService;
import java.util.Locale;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

/** Serves a receipt to the claim owner or to a reviewer. */
@Controller
public class ReceiptController {

    private final ClaimService claimService;
    private final ReceiptStorageService receiptStorageService;

    public ReceiptController(ClaimService claimService, ReceiptStorageService receiptStorageService) {
        this.claimService = claimService;
        this.receiptStorageService = receiptStorageService;
    }

    @GetMapping("/receipts/{claimId}")
    public ResponseEntity<Resource> receipt(@PathVariable Long claimId, Authentication authentication) {
        Claim claim = claimService.findById(claimId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        boolean reviewer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_REVIEWER"));
        if (!reviewer && !claim.getEmployeeName().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        if (claim.getReceiptPath() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No receipt for this claim");
        }
        Resource resource = receiptStorageService.load(claim.getReceiptPath());
        return ResponseEntity.ok()
                .contentType(mediaTypeFor(claim.getReceiptPath()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + claim.getReceiptPath() + "\"")
                .body(resource);
    }

    private static MediaType mediaTypeFor(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        return MediaType.IMAGE_JPEG;
    }
}