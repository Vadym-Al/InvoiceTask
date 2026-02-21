package com.invoice.task.invoicetask.repository;

import com.invoice.task.invoicetask.entity.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LineItemRepository extends JpaRepository<LineItem, UUID> {
}
