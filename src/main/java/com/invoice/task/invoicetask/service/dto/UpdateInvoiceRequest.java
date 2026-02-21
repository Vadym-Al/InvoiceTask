package com.invoice.task.invoicetask.service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateInvoiceRequest(
        @NotBlank String customerName,
        List<LineItemRequest> items
) {}
