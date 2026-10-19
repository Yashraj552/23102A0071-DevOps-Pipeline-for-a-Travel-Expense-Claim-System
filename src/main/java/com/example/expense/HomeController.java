package com.example.expense;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${app.environment:local}")
    private String environment;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "Travel Expense Claim System");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("environment", environment);
        return "index";
    }
}