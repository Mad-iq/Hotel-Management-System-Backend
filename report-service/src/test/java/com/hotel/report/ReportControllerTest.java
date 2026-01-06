package com.hotel.report;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hotel.report.controller.ReportController;
import com.hotel.report.dto.BookingSummaryReportResponse;
import com.hotel.report.service.BookingSummaryReportService;
import com.hotel.report.service.RevenueReportService;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RevenueReportService revenueReportService;

    @MockBean
    private BookingSummaryReportService bookingSummaryReportService;

    @Test
    void getRevenueReport_success() throws Exception {
        when(revenueReportService.calculateTotalRevenue())
                .thenReturn(BigDecimal.valueOf(10000));

        mockMvc.perform(get("/api/reports/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(10000))
                .andExpect(jsonPath("$.currency").value("INR"));
    }

    @Test
    void getBookingSummary_success() throws Exception {
        BookingSummaryReportResponse response =
                new BookingSummaryReportResponse(5, 3, 2);

        when(bookingSummaryReportService.getBookingSummary())
                .thenReturn(response);

        mockMvc.perform(get("/api/reports/bookings/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBookings").value(5))
                .andExpect(jsonPath("$.confirmedBookings").value(3))
                .andExpect(jsonPath("$.cancelledBookings").value(2));
    }
}
