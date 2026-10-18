package com.example.expense.web;

import com.example.expense.model.Claim;
import com.example.expense.service.ClaimService;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("claims", claimService.findAll());
        return "claims";
    }

    @GetMapping("/new")
    public String newClaim(Model model) {
        model.addAttribute("claimForm", new ClaimForm());
        return "claim-form";
    }

    @PostMapping
    public String submit(@ModelAttribute ClaimForm claimForm, Principal principal,
                         RedirectAttributes redirectAttributes) {
        // Receipt upload and validation are added on feature/validation-receipts.
        claimService.submit(claimForm, principal.getName(), null);
        redirectAttributes.addFlashAttribute("message", "Claim submitted successfully");
        return "redirect:/claims";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Claim claim = claimService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        model.addAttribute("claim", claim);
        return "claim-detail";
    }
}