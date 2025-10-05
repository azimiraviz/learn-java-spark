package com.learning.java.spark.rest_api_demo.service;

import com.learning.java.spark.rest_api_demo.model.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service layer for Product operations
 * In-memory storage for demonstration purposes
 */
public class ProductService {
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private int idCounter = 1;

    public ProductService() {
        // Seed with sample data
        seedData();
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        return products.values().stream().filter(p -> category.equalsIgnoreCase(p.getCategory())).collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    public Optional<Product> getProductById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    /**
     * Create a new product
     */
    public Product createProduct(Product product) {
        product.validate();

        String id = String.valueOf(idCounter++);
        product.setId(id);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        products.put(id, product);
        return product;
    }

    /**
     * Update an existing product
     */
    public Optional<Product> updateProduct(String id, Product updatedProduct) {
        Product existing = products.get(id);
        if (existing == null) {
            return Optional.empty();
        }

        updatedProduct.validate();
        updatedProduct.setId(id);
        updatedProduct.setCreatedAt(existing.getCreatedAt());
        updatedProduct.setUpdatedAt(LocalDateTime.now());

        products.put(id, updatedProduct);
        return Optional.of(updatedProduct);
    }

    /**
     * Delete a product
     */
    public boolean deleteProduct(String id) {
        return products.remove(id) != null;
    }

    /**
     * Check if product exists
     */
    public boolean exists(String id) {
        return products.containsKey(id);
    }

    /**
     * Get product count
     */
    public int getProductCount() {
        return products.size();
    }

    /**
     * Clear all products (useful for testing)
     */
    public void clearAll() {
        products.clear();
        idCounter = 1;
    }

    /**
     * Seed initial data
     */
    private void seedData() {
        createProduct(new Product(null, "Laptop", "High-performance laptop", 999.99, 10, "Electronics"));
        createProduct(new Product(null, "Mouse", "Wireless mouse", 29.99, 50, "Electronics"));
        createProduct(new Product(null, "Keyboard", "Mechanical keyboard", 89.99, 30, "Electronics"));
        createProduct(new Product(null, "Desk Chair", "Ergonomic office chair", 299.99, 15, "Furniture"));
        createProduct(new Product(null, "Monitor", "27-inch 4K monitor", 399.99, 20, "Electronics"));
    }
}