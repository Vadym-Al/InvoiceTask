package com.invoice.task.invoicetask.service;

import com.invoice.task.invoicetask.service.dto.CreateInvoiceRequest;
import com.invoice.task.invoicetask.service.dto.InvoiceResponse;
import com.invoice.task.invoicetask.service.dto.UpdateInvoiceRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    InvoiceResponse createInvoice(CreateInvoiceRequest request);

    InvoiceResponse getInvoice(UUID id);

    List<InvoiceResponse> getAllInvoices();

    InvoiceResponse updateInvoice(UUID id, UpdateInvoiceRequest request);

    void deleteInvoice(UUID id);

    void payInvoice(UUID invoiceId, BigDecimal amount);

    void refundInvoice(UUID invoiceId, BigDecimal amount);
}
