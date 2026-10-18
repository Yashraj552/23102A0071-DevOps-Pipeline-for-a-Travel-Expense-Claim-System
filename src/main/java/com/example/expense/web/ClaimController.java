package com.example.expense.web;

import com.example.expense.model.Claim;
import com.example.expense.model.ClaimStatus;
import com.example.expense.service.ClaimService;
import com.example.expense.service.ReceiptStorageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final ReceiptStorageService receiptStorageService;

    public ClaimController(ClaimService claimService, ReceiptStorageService receiptStorageService) {
        this.claimService = claimService;
        this.receiptStorageService = receiptStorageService;
    }

    @GetMapping
    public String list(Principal principal, Model model) {
        List<Claim> claims = claimService.findByEmployee(principal.getName());
        model.addAttribute("claims", claims);
        model.addAttribute("submittedCount", count(claims, ClaimStatus.SUBMITTED));
        model.addAttribute("approvedCount", count(claims, ClaimStatus.APPROVED));
        model.addAttribute("rejectedCount", count(claims, ClaimStatus.REJECTED));
        return "claims";
    }

    @GetMapping("/new")
    public String newClaim(Model model) {
        model.addAttribute("claimForm", new ClaimForm());
        return "claim-form";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("claimForm") ClaimForm claimForm,
                         BindingResult bindingResult,
                         @RequestParam(value = "receipt", required = false) MultipartFile receipt,
                         Principal principal,
                         Model model,
                         RedirectAttributes redirectAttributes) throws IOException {
        String receiptError = receiptStorageService.validate(receipt);
        if (bindingResult.hasErrors() || receiptError != null) {
            if (receiptError != null) {
                model.addAttribute("receiptError", receiptError);
            }
            return "claim-form";
        }
        String receiptPath = receiptStorageService.store(receipt);
        claimService.submit(claimForm, principal.getName(), receiptPath);
        redirectAttributes.addFlashAttribute("message", "Claim submitted successfully");
        return "redirect:/claims";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        Claim claim = claimService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        if (!claim.getEmployeeName().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This claim belongs to another employee");
        }
        model.addAttribute("claim", claim);
        return "claim-detail";
    }

    private static long count(List<Claim> claims, ClaimStatus status) {
        return claims.stream().filter(c -> c.getStatus() == status).count();
    }
}