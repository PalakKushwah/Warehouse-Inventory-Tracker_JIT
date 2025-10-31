package com.inventory.core;

import java.util.HashMap;
import java.util.Map;

public class WarehouseManager {
    private Map<String, Warehouse> warehouses = new HashMap<>();

    public void addWarehouse(Warehouse warehouse) {
        warehouses.put(warehouse.getName(), warehouse);
        System.out.println(" Warehouse added: " + warehouse.getName());
    }

    public Warehouse getWarehouse(String name) {
        return warehouses.get(name);
    }

    public void listWarehouses() {
        if (warehouses.isEmpty()) {
            System.out.println(" No warehouses available.");
            return;
        }
        System.out.println(" Available Warehouses:");
        for (String name : warehouses.keySet()) {
            System.out.println("- " + name);
        }
    }

    public boolean exists(String name) {
        return warehouses.containsKey(name);
    }
}
