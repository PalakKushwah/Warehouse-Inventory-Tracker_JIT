package com.inventory.observer;

import com.inventory.model.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class AlertService implements StockObserver {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onStockChanged(Product product) {
        if (product == null) return;

        int qty, threshold;
        synchronized (product) {
            qty = product.getQuantity();
            threshold = product.getReorderThreshold();
        }

        if (qty < threshold) {
            String msg = String.format("%s - RESTOCK ALERT: Low stock for %s (ID: %s) — only %d left (threshold %d).",
                    LocalDateTime.now().format(dtf), product.getName(), product.getId(), qty, threshold);
            // In production, replace println with a logger or notification sender.
            System.out.println(msg);
        }
    }
}
