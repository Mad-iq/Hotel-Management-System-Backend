package com.hotel.hotels;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.hotels.exception.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/test-exceptions")
class TestExceptionController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("Hotel not found");
    }

    @GetMapping("/entity-not-found")
    public void entityNotFound() {
        throw new EntityNotFoundException("Entity missing");
    }

    @GetMapping("/data-integrity")
    public void dataIntegrity() {
        throw new DataIntegrityViolationException("Duplicate key");
    }

    @GetMapping("/generic")
    public void generic() {
        throw new RuntimeException("Something went wrong");
    }
}
