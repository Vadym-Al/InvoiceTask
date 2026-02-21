package com.invoice.task.invoicetask.service;

import com.invoice.task.invoicetask.entity.Invoice;

import java.math.BigDecimal;

public interface PaymentService {
    void pay(Invoice invoice, BigDecimal amount);
    void refund(Invoice invoice, BigDecimal amount);
}
