package com.invoice.task.invoicetask.service.impl;

import com.invoice.task.invoicetask.entity.Invoice;
import com.invoice.task.invoicetask.entity.LineItem;
import com.invoice.task.invoicetask.entity.enums.InvoiceStatus;
import com.invoice.task.invoicetask.exception.InvoiceNotFoundException;
import com.invoice.task.invoicetask.repository.InvoiceRepository;
import com.invoice.task.invoicetask.service.InvoiceService;
import com.invoice.task.invoicetask.service.PaymentService;
import com.invoice.task.invoicetask.service.dto.CreateInvoiceRequest;
import com.invoice.task.invoicetask.service.dto.InvoiceResponse;
import com.invoice.task.invoicetask.service.dto.LineItemResponse;
import com.invoice.task.invoicetask.service.dto.UpdateInvoiceRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentService paymentService;


    @Override
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setCustomerName(request.customerName());

        request.items().forEach(itemReq -> {
            LineItem item = new LineItem();
            item.setInvoice(invoice);
            item.setDescription(itemReq.description());
            item.setPrice(itemReq.price());
            item.setQuantity(itemReq.quantity());
            invoice.getItems().add(item);
        });

        recalculateTotal(invoice);
        invoice.setStatus(InvoiceStatus.ISSUED);

        Invoice saved = invoiceRepository.save(invoice);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InvoiceResponse getInvoice(UUID id) {
        return mapToResponse(findInvoice(id));
    }

    @Override
    @Transactional
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public InvoiceResponse updateInvoice(UUID id, UpdateInvoiceRequest request) {
        Invoice invoice = findInvoice(id);

        invoice.setCustomerName(request.customerName());
        invoice.getItems().clear();

        request.items().forEach(itemReq -> {
            LineItem item = new LineItem();
            item.setInvoice(invoice);
            item.setDescription(itemReq.description());
            item.setPrice(itemReq.price());
            item.setQuantity(itemReq.quantity());
            invoice.getItems().add(item);
        });

        recalculateTotal(invoice);

        return mapToResponse(invoice);
    }

    @Override
    public void deleteInvoice(UUID id) {
        invoiceRepository.deleteById(id);
    }

    @Override
    public void payInvoice(UUID invoiceId, BigDecimal amount) {
        Invoice invoice = findInvoice(invoiceId);
        paymentService.pay(invoice, amount);
    }

    @Override
    public void refundInvoice(UUID invoiceId, BigDecimal amount) {
        Invoice invoice = findInvoice(invoiceId);
        paymentService.refund(invoice, amount);
    }

    private Invoice findInvoice(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new InvoiceNotFoundException("Invoice not found: " + id));
    }

    private void recalculateTotal(Invoice invoice) {
        BigDecimal total = invoice.getItems().stream()
                .map(LineItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalAmount(total);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {

        List<LineItemResponse> items = invoice.getItems().stream()
                .map(item -> new LineItemResponse(
                        item.getId(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getTotal()
                ))
                .toList();

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getCustomerName(),
                invoice.getTotalAmount(),
                invoice.getPaidAmount(),
                invoice.getStatus().name(),
                invoice.getCreatedAt(),
                items
        );
    }
}
