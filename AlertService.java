package com.inventory.observer;

import com.inventory.model.Product;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertService implements StockObserver {
    @Override
    public void onLowStock(Product product) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(time + " - RESTOCK ALERT: Low stock for " +
                product.getName() + " (ID: " + product.getId() + ") — only " +
                product.getQuantity() + " left (threshold " + product.getReorderThreshold() + ").");
    }
}
