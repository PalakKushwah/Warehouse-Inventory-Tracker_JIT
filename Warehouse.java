package com.inventory.core;

import com.inventory.model.Product;
import com.inventory.observer.StockObserver;
import com.inventory.exceptions.InsufficientStockException;
import com.inventory.exceptions.ProductNotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Warehouse {

    private final String warehouseId;
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final List<StockObserver> observers = new CopyOnWriteArrayList<>();

    public Warehouse(String warehouseId) {
        if (warehouseId == null || warehouseId.isEmpty()) throw new IllegalArgumentException("warehouseId required");
        this.warehouseId = warehouseId;
    }

    public String getWarehouseId() { return warehouseId; }

    public void addObserver(StockObserver observer) {
        if (observer != null) observers.add(observer);
    }

    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Product p) {
        for (StockObserver obs : observers) {
            try {
                obs.onStockChanged(p);
            } catch (Exception ex) {
                // Observers should not break warehouse flow
                System.err.println("Observer error: " + ex.getMessage());
            }
        }
    }

    public void addProduct(Product product) {
        Objects.requireNonNull(product, "product required");
        Product prev = products.putIfAbsent(product.getId(), product);
        if (prev != null) {
            throw new IllegalArgumentException("Product with id already exists: " + product.getId());
        }
    }

    public Product getProduct(String id) throws ProductNotFoundException {
        Product p = products.get(id);
        if (p == null) throw new ProductNotFoundException(id);
        return p;
    }

    public Collection<Product> listProducts() { return Collections.unmodifiableCollection(products.values()); }

    public int receiveShipment(String productId, int amount) throws ProductNotFoundException {
        Product p = getProduct(productId);
        int newQty;
        synchronized (p) {
            newQty = p.increase(amount);
        }
        notifyObservers(p);
        return newQty;
    }

    /**
     * Fulfill an order (decrease stock). Throws InsufficientStockException when not enough.
     */
    public int fulfillOrder(String productId, int amount) throws ProductNotFoundException, InsufficientStockException {
        Product p = getProduct(productId);
        int newQty;
        synchronized (p) {
            if (amount > p.getQuantity()) {
                throw new InsufficientStockException(productId, amount, p.getQuantity());
            }
            newQty = p.decrease(amount);
        }
        notifyObservers(p);
        return newQty;
    }

    
    public void replaceInventory(Map<String, Product> newInventory) {
        products.clear();
        products.putAll(newInventory);
        // Notify observers for all products to recalc alerts
        for (Product p : products.values()) notifyObservers(p);
    }

    public boolean contains(String productId) {
        return products.containsKey(productId);
    }
}

