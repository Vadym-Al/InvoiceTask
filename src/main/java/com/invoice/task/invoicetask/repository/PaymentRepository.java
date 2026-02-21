package com.invoice.task.invoicetask.repository;

import com.invoice.task.invoicetask.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
