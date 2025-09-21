package com.zeta.DigitalFixedDepositSystem.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class HealthCheckController {
    @GetMapping("/health")
    @CrossOrigin(origins = "http://localhost:5173")
    public String health() {
        return "Welcome to the Digital Fixed Deposit System!!!!!!";
    }
}
