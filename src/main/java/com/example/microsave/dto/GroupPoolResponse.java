package com.example.microsave.dto;

import java.math.BigDecimal;

public record GroupPoolResponse(

        Long groupId,
        BigDecimal totalContributions,
        BigDecimal outstandingLoans,
        BigDecimal availablePool

) {
}