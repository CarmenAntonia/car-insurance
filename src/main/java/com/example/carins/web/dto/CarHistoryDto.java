package com.example.carins.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CarHistoryDto(
        CarEvent type,
        LocalDate date,
        String description,
        BigDecimal amount
){}
