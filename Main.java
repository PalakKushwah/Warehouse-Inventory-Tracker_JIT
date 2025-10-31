package com.inventory.app;

import com.inventory.core.*;
import com.inventory.model.Product;
import com.inventory.observer.AlertService;
import com.inventory.persistence.InventoryPersistence;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        WarehouseManager manager = new WarehouseManager();
        AlertService alertService = new AlertService();

        System.out.println(" Multi-Warehouse Inventory System Started!");

        while (true) {
            System.out.println("\n==== Main Menu ====");
            System.out.println("1️. Add new warehouse");
            System.out.println("2️. Switch to warehouse");
            System.out.println("3️. List all warehouses");
            System.out.println("4️. Exit");
            System.out.print("👉 Enter your choice: ");
            int mainChoice = sc.nextInt();
            sc.nextLine();

            switch (mainChoice) {
                case 1:
                    System.out.print("Enter Warehouse Name: ");
                    String wName = sc.nextLine();
                    if (manager.exists(wName)) {
                        System.out.println(" Warehouse already exists!");
                    } else {
                        Warehouse w = new Warehouse(wName);
                        w.addObserver(alertService);
                        manager.addWarehouse(w);
                    }
                    break;

                case 2:
                    manager.listWarehouses();
                    System.out.print("Enter warehouse name to switch: ");
                    String select = sc.nextLine();
                    Warehouse warehouse = manager.getWarehouse(select);
                    if (warehouse == null) {
                        System.out.println("❌ Warehouse not found!");
                        break;
                    }
                    InventoryPersistence persistence = new InventoryPersistence("data/inventory_" + select + ".csv");
                    warehouse.replaceInventory(persistence.loadInventory());

                    while (true) {
                        System.out.println("\n==== Warehouse: " + select + " ====");
                        System.out.println("1️. Add new product");
                        System.out.println("2️. Receive shipment");
                        System.out.println("3️. Fulfill order");
                        System.out.println("4️. Show inventory");
                        System.out.println("5️. Back to main menu");
                        System.out.print("👉 Enter your choice: ");
                        int choice = sc.nextInt();
                        sc.nextLine();

                        if (choice == 5) {
                            persistence.saveInventory(warehouse.getInventory());
                            System.out.println(" Inventory saved for " + select);
                            break;
                        }

                        try {
                            switch (choice) {
                                case 1:
                                    System.out.print("Enter Product ID: ");
                                    String id = sc.nextLine();
                                    System.out.print("Enter Product Name: ");
                                    String name = sc.nextLine();
                                    System.out.print("Enter Quantity: ");
                                    int qty = sc.nextInt();
                                    System.out.print("Enter Reorder Threshold: ");
                                    int th = sc.nextInt();
                                    warehouse.addProduct(new Product(id, name, qty, th));
                                    break;

                                case 2:
                                    System.out.print("Enter Product ID: ");
                                    String pid1 = sc.nextLine();
                                    System.out.print("Enter Shipment Quantity: ");
                                    int addQty = sc.nextInt();
                                    warehouse.receiveShipment(pid1, addQty);
                                    break;

                                case 3:
                                    System.out.print("Enter Product ID: ");
                                    String pid2 = sc.nextLine();
                                    System.out.print("Enter Order Quantity: ");
                                    int ordQty = sc.nextInt();
                                    warehouse.fulfillOrder(pid2, ordQty);
                                    break;

                                case 4:
                                    warehouse.printInventory();
                                    break;

                                default:
                                    System.out.println("❌ Invalid choice!");
                            }
                        } catch (Exception e) {
                            System.out.println("⚠️ Error: " + e.getMessage());
                        }
                    }
                    break;

                case 3:
                    manager.listWarehouses();
                    break;

                case 4:
                    System.out.println("👋 Exiting System. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("❌ Invalid choice!");
            }
        }
    }
}
