package com.inventory.exceptions;

public class InsufficientStockException extends Exception {
    public InsufficientStockException(String productId, int requested, int available) {
        super(String.format("Insufficient stock for %s: requested=%d, available=%d", productId, requested, available));
    }
}

