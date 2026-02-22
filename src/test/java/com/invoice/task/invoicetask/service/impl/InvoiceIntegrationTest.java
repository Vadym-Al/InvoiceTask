package com.invoice.task.invoicetask.service.impl;

import com.invoice.task.invoicetask.service.dto.CreateInvoiceRequest;
import com.invoice.task.invoicetask.service.dto.LineItemRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvoiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullInvoiceLifecycle_shouldWorkCorrectly() throws Exception {

        CreateInvoiceRequest request = new CreateInvoiceRequest(
                "John",
                List.of(
                        new LineItemRequest("Item1", new BigDecimal("50.00"), 2)
                )
        );

        String response = mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(100.00))
                .andExpect(jsonPath("$.status").value("ISSUED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();


        mockMvc.perform(post("/api/invoices/{id}/payments", id)
                        .param("amount", "40.00"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paidAmount").value(40.00))
                .andExpect(jsonPath("$.status").value("PARTIALLY_PAID"));


        mockMvc.perform(post("/api/invoices/{id}/payments", id)
                        .param("amount", "60.00"))
                .andExpect(status().isOk());


        mockMvc.perform(get("/api/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paidAmount").value(100.00))
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}
