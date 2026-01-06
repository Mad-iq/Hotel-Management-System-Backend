package com.hotel.payment;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.hotel.payment.controller.PaymentController;
import com.hotel.payment.exception.GlobalExceptionHandler;
import com.hotel.payment.service.PaymentService;

@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void handleIllegalState_returnsBadRequest() throws Exception {
        when(paymentService.createPayment(1L, 10L))
                .thenThrow(new IllegalStateException("Payment already exists"));

        mockMvc.perform(post("/api/payments")
                        .param("bookingId", "1")
                        .header("X-USER-ID", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Payment already exists"))
                .andExpect(jsonPath("$.path").value("/api/payments"));
    }


    @Test
    void handleGenericException_returnsInternalServerError() throws Exception {
        when(paymentService.getPaymentByBookingId(1L, null))
                .thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(get("/api/payments/booking/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Something went wrong"))
                .andExpect(jsonPath("$.path").value("/api/payments/booking/1"));
    }
}
