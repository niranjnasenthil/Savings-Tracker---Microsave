package com.example.microsave.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RepaymentRequest(

        @NotNull(message = "Amount is required")
        @Positive(message = "Repayment must be greater than zero")
        BigDecimal amount
) {
}