package com.aichatbot.customerservices.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/overview")
    public AdminOverviewResponse overview() {
        return new AdminOverviewResponse(
                "ADMIN",
                List.of("CUSTOMER", "AGENT", "MANAGER", "ADMIN"),
                "Role-based access control is active.");
    }
}

