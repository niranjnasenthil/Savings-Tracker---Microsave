package com.example.microsave.service;

import com.example.microsave.dto.*;
import com.example.microsave.model.*;
import com.example.microsave.exception.BussinessException;
import com.example.microsave.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MicroSaveService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ContributionRepository contributionRepository;
    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;

    public Group createGroup(CreateGroupRequest request) {

        Group group = new Group();

        group.setName(request.name().trim());

        return groupRepository.save(group);
    }

    public Member addMember(
            Long groupId,
            CreateMemberRequest request) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new BussinessException(
                                "Group not found: " + groupId));

        Member member = new Member();

        member.setName(request.name().trim());
        member.setGroup(group);

        return memberRepository.save(member);
    }

    @Transactional
    public Contribution addContribution(
            MoneyRequest request) {

        Member member = getMember(request.memberId());

        Contribution contribution = new Contribution();

        contribution.setMember(member);
        contribution.setAmount(request.amount());
        contribution.setContributionDate(LocalDate.now());

        return contributionRepository.save(contribution);
    }

    @Transactional
    public Loan disburseLoan(
            MoneyRequest request) {

        Member member = getMember(request.memberId());

        // Business Rule 1:
        // Member cannot take another loan
        // when an unpaid loan already exists.

        if (loanRepository
                .countByMember_IdAndOutstandingAmountGreaterThan(
                        member.getId(),
                        BigDecimal.ZERO) > 0) {

            throw new BussinessException(
                    "This member already has an active unpaid loan.");
        }

        // Calculate group available pool

        GroupPoolResponse pool =
                getGroupPool(member.getGroup().getId());

        // Business Rule 2:
        // Loan cannot exceed available group pool.

        if (request.amount()
                .compareTo(pool.availablePool()) > 0) {

            throw new BussinessException(
                    "Loan rejected. Requested amount exceeds the group's available pool of ₹"
                            + pool.availablePool());
        }

        Loan loan = new Loan();

        loan.setMember(member);
        loan.setAmount(request.amount());
        loan.setOutstandingAmount(request.amount());
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLoanDate(LocalDate.now());

        return loanRepository.save(loan);
    }

    @Transactional
    public Repayment repayLoan(
            Long loanId,
            RepaymentRequest request) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new BussinessException(
                                "Loan not found: " + loanId));

        if (loan.getOutstandingAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new BussinessException(
                    "This loan is already fully paid.");
        }

        if (request.amount()
                .compareTo(loan.getOutstandingAmount()) > 0) {

            throw new BussinessException(
                    "Repayment cannot exceed outstanding loan of ₹"
                            + loan.getOutstandingAmount());
        }

        loan.setOutstandingAmount(
                loan.getOutstandingAmount()
                        .subtract(request.amount())
        );

        if (loan.getOutstandingAmount()
                .compareTo(BigDecimal.ZERO) == 0) {

            loan.setStatus(LoanStatus.PAID);
        }

        loanRepository.save(loan);

        Repayment repayment = new Repayment();

        repayment.setLoan(loan);
        repayment.setAmount(request.amount());
        repayment.setRepaymentDate(LocalDate.now());

        return repaymentRepository.save(repayment);
    }

    public GroupPoolResponse getGroupPool(
            Long groupId) {

        if (!groupRepository.existsById(groupId)) {

            throw new BussinessException(
                    "Group not found: " + groupId);
        }

        BigDecimal contributions =
                contributionRepository
                        .totalByGroup(groupId);

        BigDecimal outstanding =
                loanRepository
                        .outstandingByGroup(groupId);

        BigDecimal available =
                contributions.subtract(outstanding);

        return new GroupPoolResponse(
                groupId,
                contributions,
                outstanding,
                available
        );
    }

    public MemberSummaryResponse getMemberSummary(
            Long memberId) {

        Member member = getMember(memberId);

        BigDecimal savings =
                contributionRepository
                        .totalByMember(memberId);

        BigDecimal outstanding =
                loanRepository
                        .outstandingByMember(memberId);

        return new MemberSummaryResponse(
                memberId,
                member.getName(),
                savings,
                outstanding
        );
    }

    private Member getMember(Long memberId) {

        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BussinessException(
                                "Member not found: " + memberId));
    }
}