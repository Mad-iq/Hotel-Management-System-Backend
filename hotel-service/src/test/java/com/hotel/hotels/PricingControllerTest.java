package com.hotel.hotels;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.hotels.controller.PricingController;
import com.hotel.hotels.dto.CategoryPricingRequestDto;
import com.hotel.hotels.dto.SeasonalPricingRequestDto;
import com.hotel.hotels.entity.CategoryPricing;
import com.hotel.hotels.entity.SeasonalPricing;
import com.hotel.hotels.service.PricingService;

@WebMvcTest(PricingController.class)
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PricingService pricingService;

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    void setBasePricing_success() throws Exception {
        CategoryPricingRequestDto request = new CategoryPricingRequestDto();
        request.setBasePrice(BigDecimal.valueOf(2000));
        request.setCurrency("INR");

        CategoryPricing pricing = new CategoryPricing();
        pricing.setId(1L);
        pricing.setBasePrice(BigDecimal.valueOf(2000));
        pricing.setCurrency("INR");

        when(pricingService.setBasePricing(eq(10L), any(CategoryPricing.class)))
                .thenReturn(pricing);

        mockMvc.perform(post("/api/categories/10/pricing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.basePrice").value(2000))
                .andExpect(jsonPath("$.currency").value("INR"));
    }


    @Test
    void getBasePricing_success() throws Exception {
        CategoryPricing pricing = new CategoryPricing();
        pricing.setId(2L);
        pricing.setBasePrice(BigDecimal.valueOf(1800));
        pricing.setCurrency("INR");

        when(pricingService.getBasePricing(10L))
                .thenReturn(pricing);

        mockMvc.perform(get("/api/categories/10/pricing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.basePrice").value(1800));
    }

    @Test
    void addSeasonalPricing_success() throws Exception {
        SeasonalPricingRequestDto request = new SeasonalPricingRequestDto();
        request.setStartDate(LocalDate.parse("2026-01-01"));
        request.setEndDate(LocalDate.parse("2026-01-10"));
        request.setPrice(BigDecimal.valueOf(2500));

        SeasonalPricing pricing = new SeasonalPricing();
        pricing.setId(5L);
        pricing.setStartDate(request.getStartDate());
        pricing.setEndDate(request.getEndDate());
        pricing.setPrice(BigDecimal.valueOf(2500));

        when(pricingService.addSeasonalPricing(eq(10L), any(SeasonalPricing.class)))
                .thenReturn(List.of(pricing));

        mockMvc.perform(post("/api/categories/10/seasonal-pricing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].price").value(2500));
    }

    @Test
    void getSeasonalPricing_success() throws Exception {
        SeasonalPricing pricing = new SeasonalPricing();
        pricing.setId(7L);
        pricing.setPrice(BigDecimal.valueOf(2200));

        when(pricingService.getSeasonalPricing(
                eq(10L),
                eq(LocalDate.parse("2026-01-05"))))
                .thenReturn(List.of(pricing));

        mockMvc.perform(get("/api/categories/10/seasonal-pricing")
                .param("date", "2026-01-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].price").value(2200));
    }
}
