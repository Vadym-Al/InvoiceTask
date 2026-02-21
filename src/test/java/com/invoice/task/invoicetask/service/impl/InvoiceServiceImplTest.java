package com.invoice.task.invoicetask.service.impl;

import com.invoice.task.invoicetask.entity.Invoice;
import com.invoice.task.invoicetask.entity.enums.InvoiceStatus;
import com.invoice.task.invoicetask.exception.InvoiceNotFoundException;
import com.invoice.task.invoicetask.repository.InvoiceRepository;
import com.invoice.task.invoicetask.service.PaymentService;
import com.invoice.task.invoicetask.service.dto.CreateInvoiceRequest;
import com.invoice.task.invoicetask.service.dto.InvoiceResponse;
import com.invoice.task.invoicetask.service.dto.LineItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvoiceServiceImplTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInvoice_shouldCalculateTotalCorrectly() {

        CreateInvoiceRequest request = new CreateInvoiceRequest(
                "John",
                List.of(
                        new LineItemRequest("Item1", new BigDecimal("10.00"), 2),
                        new LineItemRequest("Item2", new BigDecimal("5.00"), 4)
                )
        );

        when(invoiceRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response = invoiceService.createInvoice(request);

        assertEquals("John", response.customerName());
        assertEquals(new BigDecimal("40.00"), response.totalAmount());
        assertEquals("ISSUED", response.status());

        verify(invoiceRepository).save(any());
    }

    @Test
    void getInvoice_shouldReturnResponse_whenExists() {

        UUID id = UUID.randomUUID();

        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setCustomerName("John");
        invoice.setTotalAmount(new BigDecimal("100.00"));
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setStatus(InvoiceStatus.ISSUED);

        when(invoiceRepository.findById(id))
                .thenReturn(Optional.of(invoice));

        InvoiceResponse response = invoiceService.getInvoice(id);

        assertEquals(id, response.id());
        assertEquals("John", response.customerName());
        assertEquals(new BigDecimal("100.00"), response.totalAmount());
    }

    @Test
    void getInvoice_shouldThrow_whenNotFound() {

        UUID id = UUID.randomUUID();

        when(invoiceRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> invoiceService.getInvoice(id));
    }

    @Test
    void deleteInvoice_shouldCallRepository() {

        UUID id = UUID.randomUUID();

        doNothing().when(invoiceRepository).deleteById(id);

        invoiceService.deleteInvoice(id);

        verify(invoiceRepository).deleteById(id);
    }

    @Test
    void payInvoice_shouldDelegateToPaymentService() {

        UUID id = UUID.randomUUID();
        Invoice invoice = new Invoice();
        invoice.setId(id);

        when(invoiceRepository.findById(id))
                .thenReturn(Optional.of(invoice));

        doNothing().when(paymentService)
                .pay(invoice, new BigDecimal("50.00"));

        invoiceService.payInvoice(id, new BigDecimal("50.00"));

        verify(paymentService).pay(invoice, new BigDecimal("50.00"));
    }

    @Test
    void payInvoice_shouldThrow_whenInvoiceNotFound() {

        UUID id = UUID.randomUUID();

        when(invoiceRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> invoiceService.payInvoice(id, new BigDecimal("10.00")));
    }
}