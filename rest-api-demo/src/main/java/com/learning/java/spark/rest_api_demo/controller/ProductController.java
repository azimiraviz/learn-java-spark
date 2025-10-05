package com.learning.java.spark.rest_api_demo.controller;

import com.learning.java.spark.rest_api_demo.model.Product;
import com.learning.java.spark.rest_api_demo.service.ProductService;
import com.learning.java.spark.rest_api_demo.util.JsonUtil;
import spark.Route;

import java.util.List;
import java.util.Optional;

/**
 * Controller for Product REST endpoints
 */
public record ProductController(ProductService productService) {

    /**
     * GET /api/products - Get all products
     * Supports optional query parameter: ?category=Electronics
     */
    public Route getAllProducts() {
        return (req, res) -> {
            res.status(200);

            String category = req.queryParams("category");
            List<Product> products;

            if (category != null && !category.isEmpty()) {
                products = productService.getProductsByCategory(category);
            } else {
                products = productService.getAllProducts();
            }

            return JsonUtil.toJson(products);
        };
    }

    /**
     * GET /api/products/:id - Get product by ID
     */
    public Route getProductById() {
        return (req, res) -> {
            String id = req.params(":id");
            Optional<Product> product = productService.getProductById(id);

            if (product.isPresent()) {
                res.status(200);
                return JsonUtil.toJson(product.get());
            } else {
                res.status(404);
                return JsonUtil.toJson(new ErrorResponse("Product not found with id: " + id));
            }
        };
    }

    /**
     * POST /api/products - Create a new product
     * Request body should contain product JSON
     */
    public Route createProduct() {
        return (req, res) -> {
            try {
                Product product = JsonUtil.fromJson(req.body(), Product.class);
                Product created = productService.createProduct(product);

                res.status(201);
                res.header("Location", "/api/products/" + created.getId());
                return JsonUtil.toJson(created);
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.toJson(new ErrorResponse(e.getMessage()));
            }
        };
    }

    /**
     * PUT /api/products/:id - Update an existing product
     * Request body should contain updated product JSON
     */
    public Route updateProduct() {
        return (req, res) -> {
            String id = req.params(":id");

            try {
                Product product = JsonUtil.fromJson(req.body(), Product.class);
                Optional<Product> updated = productService.updateProduct(id, product);

                if (updated.isPresent()) {
                    res.status(200);
                    return JsonUtil.toJson(updated.get());
                } else {
                    res.status(404);
                    return JsonUtil.toJson(new ErrorResponse("Product not found with id: " + id));
                }
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.toJson(new ErrorResponse(e.getMessage()));
            }
        };
    }

    /**
     * DELETE /api/products/:id - Delete a product
     */
    public Route deleteProduct() {
        return (req, res) -> {
            String id = req.params(":id");
            boolean deleted = productService.deleteProduct(id);

            if (deleted) {
                res.status(204); // No Content
                return "";
            } else {
                res.status(404);
                return JsonUtil.toJson(new ErrorResponse("Product not found with id: " + id));
            }
        };
    }

    private record ErrorResponse(String error) {
    }
}
