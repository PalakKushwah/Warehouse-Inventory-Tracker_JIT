package com.inventory.persistence;

import com.inventory.model.Product;
import java.io.*;
import java.util.*;

public class InventoryPersistence {
    private String filePath;

    public InventoryPersistence(String filePath) {
        this.filePath = filePath;
    }

    public void saveInventory(Map<String, Product> inventory) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Product p : inventory.values()) {
                writer.write(p.getId() + "," + p.getName() + "," +
                        p.getQuantity() + "," + p.getReorderThreshold());
                writer.newLine();
            }
            System.out.println("💾 Inventory persisted to " + filePath);
        } catch (IOException e) {
            System.out.println("❌ Error saving inventory: " + e.getMessage());
        }
    }

    public Map<String, Product> loadInventory() {
        Map<String, Product> inventory = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) return inventory;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Product p = new Product(parts[0], parts[1],
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]));
                    inventory.put(p.getId(), p);
                }
            }
            System.out.println("📁 Loaded persisted inventory: " + inventory.size() + " items.");
        } catch (IOException e) {
            System.out.println("❌ Error loading inventory: " + e.getMessage());
        }
        return inventory;
    }
}
