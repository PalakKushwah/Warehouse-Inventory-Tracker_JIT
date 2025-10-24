package com.inventory.simulation;

import com.inventory.core.Warehouse;
import com.inventory.exceptions.InsufficientStockException;
import com.inventory.exceptions.ProductNotFoundException;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Spawns threads that simulate incoming shipments and customer orders concurrently.
 */
public class InventorySimulator {

    private final Warehouse warehouse;
    private final ExecutorService exec;
    private final Random rnd = new Random();

    public InventorySimulator(Warehouse warehouse, int threadPoolSize) {
        this.warehouse = warehouse;
        this.exec = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void simulateForSeconds(int seconds, String[] productIds) throws InterruptedException {
        int ops = Math.max(1, seconds * 4); // approximate ops count
        CountDownLatch latch = new CountDownLatch(ops);

        for (int i = 0; i < ops; i++) {
            exec.submit(() -> {
                try {
                    String pid = productIds[rnd.nextInt(productIds.length)];
                    if (rnd.nextBoolean()) {
                        // shipment
                        int amt = 1 + rnd.nextInt(10); // 1..10
                        try {
                            warehouse.receiveShipment(pid, amt);
                            System.out.printf("Shipment: +%d to %s%n", amt, pid);
                        } catch (Exception e) {
                            System.err.println("Shipment error: " + e.getMessage());
                        }
                    } else {
                        // order
                        int amt = 1 + rnd.nextInt(6); // 1..6
                        try {
                            warehouse.fulfillOrder(pid, amt);
                            System.out.printf("Order: -%d from %s%n", amt, pid);
                        } catch (InsufficientStockException | ProductNotFoundException e) {
                            System.err.println("Order failed: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Order error: " + e.getMessage());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });

            // small pacing
            try { Thread.sleep(75); } catch (InterruptedException ignored) {}
        }

        latch.await(seconds + 2, TimeUnit.SECONDS);
    }

    public void shutdown() {
        exec.shutdownNow();
    }
}

