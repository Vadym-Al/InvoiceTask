package com.invoice.task.invoicetask.repository;

import com.invoice.task.invoicetask.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
}
