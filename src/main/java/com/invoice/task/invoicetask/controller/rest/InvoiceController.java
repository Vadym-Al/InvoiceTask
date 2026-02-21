package com.invoice.task.invoicetask.controller.rest;

import com.invoice.task.invoicetask.service.InvoiceService;
import com.invoice.task.invoicetask.service.dto.CreateInvoiceRequest;
import com.invoice.task.invoicetask.service.dto.InvoiceResponse;
import com.invoice.task.invoicetask.service.dto.UpdateInvoiceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(
            @Valid @RequestBody CreateInvoiceRequest request) {

        InvoiceResponse response = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public InvoiceResponse get(@PathVariable UUID id) {
        return invoiceService.getInvoice(id);
    }

    @GetMapping
    public List<InvoiceResponse> getAll() {
        return invoiceService.getAllInvoices();
    }

    @PutMapping("/{id}")
    public InvoiceResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInvoiceRequest request) {

        return invoiceService.updateInvoice(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<Void> pay(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {

        invoiceService.payInvoice(id, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refunds")
    public ResponseEntity<Void> refund(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {

        invoiceService.refundInvoice(id, amount);
        return ResponseEntity.ok().build();
    }
}
