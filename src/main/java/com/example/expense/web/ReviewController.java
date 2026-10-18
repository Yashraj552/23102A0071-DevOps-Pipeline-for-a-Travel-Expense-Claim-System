package com.example.expense.web;

import com.example.expense.model.Claim;
import com.example.expense.service.ClaimService;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ClaimService claimService;

    public ReviewController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public String pending(Model model) {
        model.addAttribute("claims", claimService.findPending());
        return "review-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Claim claim = claimService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        model.addAttribute("claim", claim);
        return "review-detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam(value = "comment", required = false) String comment,
                          RedirectAttributes redirectAttributes) {
        try {
            claimService.approve(id, comment);
            redirectAttributes.addFlashAttribute("message", "Claim " + id + " approved");
        } catch (IllegalStateException | NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/review";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(value = "comment", required = false) String comment,
                         RedirectAttributes redirectAttributes) {
        try {
            claimService.reject(id, comment);
            redirectAttributes.addFlashAttribute("message", "Claim " + id + " rejected");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/review/" + id;
        } catch (IllegalStateException | NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/review";
    }
}