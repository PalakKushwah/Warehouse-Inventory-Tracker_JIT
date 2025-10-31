package com.inventory.model;

public class Product {
    private String id;
    private String name;
    private int quantity;
    private int reorderThreshold;

    public Product(String id, String name, int quantity, int reorderThreshold) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getReorderThreshold() { return reorderThreshold; }

    public void increaseStock(int amount) {
        if (amount > 0) this.quantity += amount;
    }

    public void decreaseStock(int amount) {
        if (amount > 0 && amount <= quantity) this.quantity -= amount;
    }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', quantity=" + quantity +
                ", reorderThreshold=" + reorderThreshold + "}";
    }
}
