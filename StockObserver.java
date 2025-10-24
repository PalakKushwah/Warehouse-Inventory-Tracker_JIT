package com.inventory.observer;

import com.inventory.model.Product;

public interface StockObserver {
   
    void onStockChanged(Product product);
}

