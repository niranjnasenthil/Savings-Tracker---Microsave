package com.example.microsave.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequest(
        @NotBlank(message = "Member name is required")
        String name
) {
}