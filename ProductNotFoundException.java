package com.inventory.exceptions;

public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String id) {
        super("Product not found: " + id);
    }
}

