package com.inventory.core;

import com.inventory.model.Product;
import com.inventory.observer.StockObserver;
import com.inventory.exceptions.ProductNotFoundException;
import com.inventory.exceptions.InsufficientStockException;
import java.util.*;

public class Warehouse {
    private String name;
    private Map<String, Product> inventory = new HashMap<>();
    private List<StockObserver> observers = new ArrayList<>();

    public Warehouse(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void addProduct(Product product) {
        if (inventory.containsKey(product.getId())) {
            throw new IllegalArgumentException("Product with id already exists: " + product.getId());
        }
        inventory.put(product.getId(), product);
        System.out.println("Added product: " + product.getName() + " (ID: " + product.getId() + ")");
    }

    public void receiveShipment(String productId, int quantity) {
        Product p = inventory.get(productId);
        if (p == null) throw new ProductNotFoundException("Product not found: " + productId);
        p.increaseStock(quantity);
        System.out.println(" Shipment received: " + quantity + " units added for " + p.getName());
    }

    public void fulfillOrder(String productId, int quantity) {
        Product p = inventory.get(productId);
        if (p == null) throw new ProductNotFoundException("Product not found: " + productId);
        if (p.getQuantity() < quantity) throw new InsufficientStockException("Insufficient stock for: " + p.getName());

        p.decreaseStock(quantity);
        System.out.println(" Order fulfilled: " + quantity + " units of " + p.getName());

        if (p.getQuantity() < p.getReorderThreshold()) {
            for (StockObserver observer : observers) observer.onLowStock(p);
        }
    }

    public void printInventory() {
        System.out.println("\n Current Inventory for " + name + ":");
        for (Product p : inventory.values()) {
            System.out.println(p);
        }
    }

    public Map<String, Product> getInventory() { return inventory; }
    public void replaceInventory(Map<String, Product> newInv) { this.inventory = newInv; }
}
