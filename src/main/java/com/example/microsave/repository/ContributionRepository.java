package com.example.microsave.repository;

import com.example.microsave.model.Contribution;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    @Query("""
        select coalesce(sum(c.amount), 0)
        from Contribution c
        where c.member.group.id = :groupId
    """)
    BigDecimal totalByGroup(@Param("groupId") Long groupId);

    @Query("""
        select coalesce(sum(c.amount), 0)
        from Contribution c
        where c.member.id = :memberId
    """)
    BigDecimal totalByMember(@Param("memberId") Long memberId);
}