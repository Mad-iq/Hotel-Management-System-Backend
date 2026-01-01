package com.hotel.report.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.report.dto.RevenueReportResponse;
import com.hotel.report.service.RevenueReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final RevenueReportService revenueReportService;

    public ReportController(RevenueReportService revenueReportService) {
        this.revenueReportService = revenueReportService;
    }

    @GetMapping("/revenue")
    public RevenueReportResponse getRevenueReport() {

        BigDecimal totalRevenue =
                revenueReportService.calculateTotalRevenue();

        return new RevenueReportResponse(totalRevenue, "INR");
    }
}
