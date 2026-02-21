package com.invoice.task.invoicetask.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LineItemRequest(
        @NotBlank String description,
        @Positive BigDecimal price,
        @Positive Integer quantity
) {}
