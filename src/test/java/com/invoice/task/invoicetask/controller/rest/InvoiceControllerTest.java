package com.invoice.task.invoicetask.controller.rest;

import com.invoice.task.invoicetask.exception.InvoiceNotFoundException;
import com.invoice.task.invoicetask.exception.OverpaymentException;
import com.invoice.task.invoicetask.service.InvoiceService;
import com.invoice.task.invoicetask.service.dto.CreateInvoiceRequest;
import com.invoice.task.invoicetask.service.dto.InvoiceResponse;
import com.invoice.task.invoicetask.service.dto.LineItemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InvoiceService invoiceService;

    @Test
    void createInvoice_shouldReturn201_whenValid() throws Exception {

        CreateInvoiceRequest request = new CreateInvoiceRequest(
                "John",
                List.of(new LineItemRequest("Item1", new BigDecimal("10.00"), 2))
        );

        InvoiceResponse response = new InvoiceResponse(
                UUID.randomUUID(),
                "John",
                new BigDecimal("20.00"),
                BigDecimal.ZERO,
                "ISSUED",
                LocalDateTime.now(),
                List.of()
        );

        when(invoiceService.createInvoice(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("John"))
                .andExpect(jsonPath("$.totalAmount").value(20.00))
                .andExpect(jsonPath("$.status").value("ISSUED"));
    }

    @Test
    void createInvoice_shouldReturn400_whenValidationFails() throws Exception {

        CreateInvoiceRequest invalidRequest =
                new CreateInvoiceRequest("", List.of());

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInvoice_shouldReturn200_whenExists() throws Exception {

        UUID id = UUID.randomUUID();

        InvoiceResponse response = new InvoiceResponse(
                id,
                "John",
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                "ISSUED",
                LocalDateTime.now(),
                List.of()
        );

        when(invoiceService.getInvoice(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.customerName").value("John"))
                .andExpect(jsonPath("$.totalAmount").value(100.00));
    }

    @Test
    void getInvoice_shouldReturn404_whenNotFound() throws Exception {

        UUID id = UUID.randomUUID();

        when(invoiceService.getInvoice(id))
                .thenThrow(new InvoiceNotFoundException("Invoice not found"));

        mockMvc.perform(get("/api/invoices/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void payInvoice_shouldReturn200_whenSuccess() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(invoiceService)
                .payInvoice(id, new BigDecimal("50.00"));

        mockMvc.perform(post("/api/invoices/{id}/payments", id)
                        .param("amount", "50.00"))
                .andExpect(status().isOk());
    }

    @Test
    void payInvoice_shouldReturn400_whenOverpayment() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new OverpaymentException("Overpayment not allowed"))
                .when(invoiceService)
                .payInvoice(id, new BigDecimal("200.00"));

        mockMvc.perform(post("/api/invoices/{id}/payments", id)
                        .param("amount", "200.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteInvoice_shouldReturn204() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(invoiceService).deleteInvoice(id);

        mockMvc.perform(delete("/api/invoices/{id}", id))
                .andExpect(status().isNoContent());
    }
}