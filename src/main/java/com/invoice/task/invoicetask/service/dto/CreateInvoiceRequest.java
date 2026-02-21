package com.invoice.task.invoicetask.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateInvoiceRequest(
        @NotBlank String customerName,
        @NotEmpty List<LineItemRequest> items
) {}
