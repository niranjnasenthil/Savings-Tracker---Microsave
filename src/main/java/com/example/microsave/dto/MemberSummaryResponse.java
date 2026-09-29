package com.example.microsave.dto;

import java.math.BigDecimal;

public record MemberSummaryResponse(

        Long memberId,
        String memberName,
        BigDecimal savingsBalance,
        BigDecimal outstandingLoan

) {
}