package com.example.microsave.controller;

import com.example.microsave.dto.*;
import com.example.microsave.model.*;
import com.example.microsave.service.MicroSaveService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MicroSaveController {

    private final MicroSaveService service;

    @PostMapping("/groups")
    public ResponseEntity<Group> createGroup(
            @Valid @RequestBody CreateGroupRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createGroup(request));
    }

    @PostMapping("/groups/{groupId}/members")
    public ResponseEntity<Member> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateMemberRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addMember(
                        groupId,
                        request));
    }

    @PostMapping("/contributions")
    public ResponseEntity<Contribution> addContribution(
            @Valid @RequestBody MoneyRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addContribution(request));
    }

    @PostMapping("/loans")
    public ResponseEntity<Loan> disburseLoan(
            @Valid @RequestBody MoneyRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.disburseLoan(request));
    }

    @PostMapping("/loans/{loanId}/repayments")
    public ResponseEntity<Repayment> repayLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody RepaymentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.repayLoan(
                        loanId,
                        request));
    }

    @GetMapping("/groups/{groupId}/pool")
    public GroupPoolResponse getGroupPool(
            @PathVariable Long groupId) {

        return service.getGroupPool(groupId);
    }

    @GetMapping("/members/{memberId}/summary")
    public MemberSummaryResponse getMemberSummary(
            @PathVariable Long memberId) {

        return service.getMemberSummary(memberId);
    }
}