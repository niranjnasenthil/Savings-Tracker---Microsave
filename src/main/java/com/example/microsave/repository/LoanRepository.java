package com.example.microsave.repository;

import com.example.microsave.model.Loan;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("""
        select coalesce(sum(l.outstandingAmount), 0)
        from Loan l
        where l.member.group.id = :groupId
        and l.outstandingAmount > 0
    """)
    BigDecimal outstandingByGroup(@Param("groupId") Long groupId);

    @Query("""
        select coalesce(sum(l.outstandingAmount), 0)
        from Loan l
        where l.member.id = :memberId
        and l.outstandingAmount > 0
    """)
    BigDecimal outstandingByMember(@Param("memberId") Long memberId);

    long countByMember_IdAndOutstandingAmountGreaterThan(
            Long memberId,
            BigDecimal amount
    );
}