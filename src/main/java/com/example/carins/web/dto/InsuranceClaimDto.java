package com.example.carins.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsuranceClaimDto(
        LocalDate claimDate,
        String description,
        BigDecimal amount
){}
