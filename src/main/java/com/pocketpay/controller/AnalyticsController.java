package com.pocketpay.controller;

import com.pocketpay.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/daily-volume")
    // @PreAuthorize("hasRole('ADMIN')") // Uncomment when Admin roles are set up
    public ResponseEntity<BigDecimal> getDailyVolume(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ResponseEntity.ok(analyticsService.getDailyTransactionVolume(date));
    }

    @GetMapping("/type-distribution")
    public ResponseEntity<Map<String, Long>> getTypeDistribution() {
        return ResponseEntity.ok(analyticsService.getTransactionTypeDistribution());
    }
}
