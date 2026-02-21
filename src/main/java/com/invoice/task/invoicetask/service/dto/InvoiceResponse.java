package com.invoice.task.invoicetask.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        String customerName,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String status,
        LocalDateTime createdAt,
        List<LineItemResponse> items
) {}
