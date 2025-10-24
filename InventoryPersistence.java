package com.inventory.persistence;

import com.inventory.model.Product;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class InventoryPersistence {

    private final Path file;

    public InventoryPersistence(String pathToFile) {
        this.file = Paths.get(pathToFile);
    }

    public void save(Collection<Product> products) throws IOException {
        Files.createDirectories(file.getParent() == null ? Paths.get(".") : file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Product p : products) {
                String line = String.format("%s,%s,%d,%d",
                        escape(p.getId()),
                        escape(p.getName()),
                        p.getQuantity(),
                        p.getReorderThreshold());
                bw.write(line);
                bw.newLine();
            }
        }
    }

    public Map<String, Product> load() throws IOException {
        Map<String, Product> map = new HashMap<>();
        if (!Files.exists(file)) return map;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = splitCsv(line);
                if (parts.length < 4) continue;
                String id = unescape(parts[0]);
                String name = unescape(parts[1]);
                int qty = Integer.parseInt(parts[2]);
                int threshold = Integer.parseInt(parts[3]);
                map.put(id, new Product(id, name, qty, threshold));
            }
        }
        return map;
    }

    // Minimal CSV escaping for commas
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"").contains(",") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }

    private String unescape(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    private String[] splitCsv(String line) {
        // naive but sufficient for simple CSV: handle quotes around fields
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                cur.append(c); // keep quotes for unescape logic
            } else if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
