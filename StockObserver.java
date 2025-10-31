package com.inventory.observer;

import com.inventory.model.Product;

public interface StockObserver {
    void onLowStock(Product product);
}
