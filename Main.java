package com.inventory.app;

import com.inventory.core.Warehouse;
import com.inventory.model.Product;
import com.inventory.observer.AlertService;
import com.inventory.persistence.InventoryPersistence;
import com.inventory.simulation.InventorySimulator;
import com.inventory.exceptions.*;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String persistenceFile = "data/inventory.csv";
        Warehouse warehouse = new Warehouse("WH-001");
        AlertService alertService = new AlertService();
        warehouse.addObserver(alertService);

        InventoryPersistence persistence = new InventoryPersistence(persistenceFile);

        // 🧾 Step 1: Try loading persisted inventory (if exists)
        try {
            Map<String, Product> loaded = persistence.load();
            if (!loaded.isEmpty()) {
                warehouse.replaceInventory(loaded);
                System.out.println("Loaded persisted inventory: " + loaded.size() + " items.");
            }
        } catch (IOException e) {
            System.err.println("Could not load persistence: " + e.getMessage());
        }

        // 🧩 Step 2: Add products only if not already present
        if (!warehouse.contains("P100")) {
            Product laptop = new Product("P100", "Laptop", 0, 5);
            warehouse.addProduct(laptop);
            System.out.println("Added product: Laptop (ID: P100)");
        }

        if (!warehouse.contains("P101")) {
            Product phone = new Product("P101", "Smartphone", 8, 3);
            warehouse.addProduct(phone);
            System.out.println("Added product: Smartphone (ID: P101)");
        }

        // 🏗 Step 3: Example workflow
        try {
            System.out.println("\nReceiving shipment of 10 Laptops...");
            warehouse.receiveShipment("P100", 10); // total = 10

            System.out.println("Fulfilling 6 orders...");
            warehouse.fulfillOrder("P100", 6);     // remaining = 4 -> alert
        } catch (ProductNotFoundException | InsufficientStockException ex) {
            System.err.println("Operation failed: " + ex.getMessage());
        }

        // ⚙️ Step 4: Multithreaded simulation demo
        InventorySimulator sim = new InventorySimulator(warehouse, 4);
        try {
            System.out.println("\nStarting multithreaded simulation for 5 seconds...");
            sim.simulateForSeconds(5, new String[]{"P100", "P101"});
        } catch (InterruptedException e) {
            System.err.println("Simulation interrupted");
        } finally {
            sim.shutdown();
        }

        // 💾 Step 5: Persist final inventory state
        try {
            persistence.save(warehouse.listProducts());
            System.out.println("\nInventory persisted to " + persistenceFile);
        } catch (IOException e) {
            System.err.println("Failed to persist: " + e.getMessage());
        }

        // 📊 Step 6: Show final result
        System.out.println("\nFinal inventory:");
        warehouse.listProducts().forEach(System.out::println);
    }
}

