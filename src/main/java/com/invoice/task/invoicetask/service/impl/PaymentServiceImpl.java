package com.invoice.task.invoicetask.service.impl;

import com.invoice.task.invoicetask.entity.Invoice;
import com.invoice.task.invoicetask.entity.Payment;
import com.invoice.task.invoicetask.entity.enums.InvoiceStatus;
import com.invoice.task.invoicetask.entity.enums.PaymentType;
import com.invoice.task.invoicetask.exception.OverpaymentException;
import com.invoice.task.invoicetask.exception.RefundExceededException;
import com.invoice.task.invoicetask.repository.PaymentRepository;
import com.invoice.task.invoicetask.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public void pay(Invoice invoice, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        BigDecimal remaining = invoice.getTotalAmount()
                .subtract(invoice.getPaidAmount());

        if (amount.compareTo(remaining) > 0) {
            throw new OverpaymentException("Overpayment not allowed");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setType(PaymentType.PAYMENT);

        invoice.setPaidAmount(invoice.getPaidAmount().add(amount));
        updateStatus(invoice);

        paymentRepository.save(payment);
    }

    @Override
    public void refund(Invoice invoice, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }

        if (amount.compareTo(invoice.getPaidAmount()) > 0) {
            throw new RefundExceededException("Refund exceeds paid amount");
        }

        Payment refund = new Payment();
        refund.setInvoice(invoice);
        refund.setAmount(amount);
        refund.setType(PaymentType.REFUND);

        invoice.setPaidAmount(invoice.getPaidAmount().subtract(amount));
        updateStatus(invoice);

        paymentRepository.save(refund);
    }

    private void updateStatus(Invoice invoice) {
        if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.ISSUED);
        } else if (invoice.getPaidAmount()
                .compareTo(invoice.getTotalAmount()) < 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PAID);
        }
    }
}
