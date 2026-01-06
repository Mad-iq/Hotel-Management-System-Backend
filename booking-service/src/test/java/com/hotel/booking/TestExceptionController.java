package com.hotel.booking;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RestController
@RequestMapping("/test-exceptions")
class TestExceptionController {

    @GetMapping("/illegal-argument")
    public void illegalArgument() {
        throw new IllegalArgumentException("Invalid input");
    }

    @GetMapping("/illegal-state")
    public void illegalState() {
        throw new IllegalStateException("Invalid state");
    }

    @GetMapping("/feign")
    public void feign() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/downstream",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                new RequestTemplate()
        );
        throw FeignException.errorStatus(
                "downstream",
                feign.Response.builder()
                        .status(503)
                        .reason("Service Unavailable")
                        .request(request)
                        .build()
        );
    }

    @GetMapping("/generic")
    public void generic() {
        throw new RuntimeException("Unexpected error");
    }
}
