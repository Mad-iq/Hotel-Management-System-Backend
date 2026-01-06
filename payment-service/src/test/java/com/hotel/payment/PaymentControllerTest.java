package com.hotel.payment;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hotel.payment.controller.PaymentController;
import com.hotel.payment.entity.Payment;
import com.hotel.payment.entity.PaymentStatus;
import com.hotel.payment.service.PaymentService;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void createPayment_success() throws Exception {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentService.createPayment(1L, 10L)).thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .param("bookingId", "1")
                        .header("X-USER-ID", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void pay_success() throws Exception {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);

        when(paymentService.markPaymentAsPaid(1L, 10L)).thenReturn(payment);

        mockMvc.perform(put("/api/payments/1/pay")
                        .header("X-USER-ID", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void getPaymentByBooking_success() throws Exception {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentService.getPaymentByBookingId(1L, null))
                .thenReturn(payment);

        mockMvc.perform(get("/api/payments/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
