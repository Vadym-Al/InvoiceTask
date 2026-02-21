package com.invoice.task.invoicetask.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LineItemResponse(
        UUID id,
        String description,
        BigDecimal price,
        Integer quantity,
        BigDecimal total
) {}
