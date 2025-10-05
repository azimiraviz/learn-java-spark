package com.learning.java.spark.rest_api_demo;

import com.learning.java.spark.rest_api_demo.controller.ProductController;
import com.learning.java.spark.rest_api_demo.service.ProductService;
import com.learning.java.spark.rest_api_demo.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;


public class RestApiApplication {
    private static final Logger log = LoggerFactory.getLogger(RestApiApplication.class);
    private static final int portNumber = 8081;

    public static void main(String[] args) {
        // Configure server
        port(portNumber);

        // Enable CORS for all routes
        enableCORS();

        // Initialize services
        ProductService productService = new ProductService();
        ProductController productController = new ProductController(productService);

        // Global exception handling
        setupExceptionHandlers();

        // API Routes
        path("/api", () -> {
            // Products endpoints
            path("/products", () -> {
                get("", productController.getAllProducts());
                get("/:id", productController.getProductById());
                post("", productController.createProduct());
                put("/:id", productController.updateProduct());
                delete("/:id", productController.deleteProduct());
            });

            // Health check endpoint
            get("/health", (req, res) -> {
                res.type("application/json");
                return JsonUtil.toJson(new HealthResponse("UP", "Service is running"));
            });
        });

        // 404 handler
        notFound((req, res) -> {
            res.type("application/json");
            return JsonUtil.toJson(new ErrorResponse(404, "Route not found"));
        });

        log.info("REST API Server started on http://localhost:{}", portNumber);
        log.info("Try: http://localhost:{}/api/health", portNumber);
    }

    /**
     * Enable CORS for the API
     */
    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.type("application/json");
        });
    }

    /**
     * Setup global exception handlers
     */
    private static void setupExceptionHandlers() {
        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.type("application/json");
            res.body(JsonUtil.toJson(new ErrorResponse(400, e.getMessage())));
        });

        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.type("application/json");
            res.body(JsonUtil.toJson(new ErrorResponse(500, "Internal server error")));
            log.error("exception with message {}", e.getMessage());
        });
    }

    record HealthResponse(String status, String message) {
    }

    record ErrorResponse(int status, String message) {
    }
}
