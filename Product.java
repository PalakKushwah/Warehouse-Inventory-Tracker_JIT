package com.inventory.model;

import java.util.Objects;

public class Product {
    private final String id;      // unique
    private String name;
    private int quantity;
    private int reorderThreshold;

    public Product(String id, String name, int quantity, int reorderThreshold) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        this.id = id;
        this.name = name;
        this.quantity = Math.max(0, quantity);
        this.reorderThreshold = Math.max(0, reorderThreshold);
    }

    public String getId() { return id; }
    public synchronized String getName() { return name; }
    public synchronized void setName(String name) { this.name = name; }

    public synchronized int getQuantity() { return quantity; }
    public synchronized void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public synchronized int getReorderThreshold() { return reorderThreshold; }
    public synchronized void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = Math.max(0, reorderThreshold);
    }

    /**
     * Atomically increase quantity and return new value.
     */
    public synchronized int increase(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
        this.quantity += amount;
        return this.quantity;
    }

    /**
     * Atomically decrease quantity if possible and return new value.
     * Throws IllegalArgumentException if amount <= 0.
     */
    public synchronized int decrease(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
        if (amount > this.quantity) throw new IllegalStateException("insufficient stock");
        this.quantity -= amount;
        return this.quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Product{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", quantity=" + quantity +
               ", reorderThreshold=" + reorderThreshold +
               '}';
    }
}
