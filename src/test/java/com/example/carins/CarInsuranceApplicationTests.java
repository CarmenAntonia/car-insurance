package com.example.carins;

import com.example.carins.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, "2024-06-01"));
        assertTrue(service.isInsuranceValid(1L, "2025-06-01"));
        assertFalse(service.isInsuranceValid(2L, "2025-02-01"));
    }

    @Test
    void carNotFound() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(999L, "2025-06-01"));
        assertEquals("404 NOT_FOUND \"Car with id 999 not found\"", ex.getMessage());
    }

    @Test
    void yearTooEarly() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, "1949-06-01"));
        assertTrue(ex.getMessage().contains("Year 1949 is outside supported range"));
    }

    @Test
    void yearTooLate() {
        int nextYearPlus = java.time.Year.now().getValue() + 2;
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, nextYearPlus + "-06-01"));
        assertTrue(ex.getMessage().contains("outside supported range"));
    }

    @Test
    void monthTooLow() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, "2025-00-10"));
        assertTrue(ex.getMessage().contains("Month 0 is invalid"));
    }

    @Test
    void monthTooHigh() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, "2025-13-10"));
        assertTrue(ex.getMessage().contains("Month 13 is invalid"));
    }

    @Test
    void dayTooLow() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, "2025-06-00"));
        assertTrue(ex.getMessage().contains("Day 0 is invalid"));
    }

    @Test
    void dayTooHighForMonth() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, "2025-02-45"));
        assertTrue(ex.getMessage().contains("Day 45 is invalid"));
    }

    @Test
    void invalidFormat() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, "06-01-2025"));
        assertTrue(ex.getMessage().contains("Invalid date format"));
    }

    @Test
    void nullCarId() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(null, "2025-06-01"));
        assertTrue(ex.getMessage().contains("carId and date must be provided"));
    }

    @Test
    void nullDate() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, null));
        assertTrue(ex.getMessage().contains("carId and date must be provided"));
    }
}
