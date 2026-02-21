package com.invoice.task.invoicetask.service.impl;

import com.invoice.task.invoicetask.entity.Invoice;
import com.invoice.task.invoicetask.entity.Payment;
import com.invoice.task.invoicetask.entity.enums.InvoiceStatus;
import com.invoice.task.invoicetask.exception.OverpaymentException;
import com.invoice.task.invoicetask.exception.RefundExceededException;
import com.invoice.task.invoicetask.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Invoice invoice(BigDecimal total, BigDecimal paid) {
        Invoice i = new Invoice();
        i.setTotalAmount(total);
        i.setPaidAmount(paid);
        i.setStatus(InvoiceStatus.ISSUED);
        return i;
    }

    @Test
    void pay_shouldUpdatePaidAmountAndStatus_partially() {
        Invoice invoice = invoice(new BigDecimal("100.00"), BigDecimal.ZERO);

        paymentService.pay(invoice, new BigDecimal("40.00"));

        assertEquals(new BigDecimal("40.00"), invoice.getPaidAmount());
        assertEquals(InvoiceStatus.PARTIALLY_PAID, invoice.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void pay_shouldMarkAsPaid_whenFullyPaid() {
        Invoice invoice = invoice(new BigDecimal("100.00"), BigDecimal.ZERO);

        paymentService.pay(invoice, new BigDecimal("100.00"));

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }

    @Test
    void pay_shouldThrowOverpaymentException() {
        Invoice invoice = invoice(new BigDecimal("100.00"), new BigDecimal("90.00"));

        assertThrows(OverpaymentException.class,
                () -> paymentService.pay(invoice, new BigDecimal("20.00")));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void refund_shouldDecreasePaidAmount() {
        Invoice invoice = invoice(new BigDecimal("100.00"), new BigDecimal("70.00"));

        paymentService.refund(invoice, new BigDecimal("20.00"));

        assertEquals(new BigDecimal("50.00"), invoice.getPaidAmount());
        assertEquals(InvoiceStatus.PARTIALLY_PAID, invoice.getStatus());
    }

    @Test
    void refund_shouldThrowRefundExceededException() {
        Invoice invoice = invoice(new BigDecimal("100.00"), new BigDecimal("40.00"));

        assertThrows(RefundExceededException.class,
                () -> paymentService.refund(invoice, new BigDecimal("50.00")));

        verify(paymentRepository, never()).save(any());
    }
}
