package com.learning.java.spark.rest_api_demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the REST API
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductApiTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // Start the application in a separate thread
        new Thread(() -> RestApiApplication.main(new String[]{})).start();

        // Wait for server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void testHealthEndpoint() {
        given()
                .when()
                .get("/api/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("message", equalTo("Service is running"));
    }

    @Test
    @Order(2)
    public void testGetAllProducts() {
        given()
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
    @Order(3)
    public void testGetProductById() {
        given()
                .when()
                .get("/api/products/1")
                .then()
                .statusCode(200)
                .body("id", equalTo("1"))
                .body("name", notNullValue());
    }

    @Test
    @Order(4)
    public void testGetProductByIdNotFound() {
        given()
                .when()
                .get("/api/products/999")
                .then()
                .statusCode(404)
                .body("error", containsString("not found"));
    }

    @Test
    @Order(5)
    public void testCreateProduct() {
        String newProduct = """
            {
                "name": "Test Product",
                "description": "A test product",
                "price": 99.99,
                "quantity": 5,
                "category": "Test"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(newProduct)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Product"))
                .body("price", equalTo(99.99f))
                .body("id", notNullValue())
                .header("Location", containsString("/api/products/"));
    }

    @Test
    @Order(6)
    public void testCreateProductWithInvalidData() {
        String invalidProduct = """
            {
                "description": "Missing name",
                "price": -10,
                "quantity": 5
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidProduct)
                .when()
                .post("/api/products")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    public void testUpdateProduct() {
        String updatedProduct = """
            {
                "name": "Updated Laptop",
                "description": "Updated description",
                "price": 1299.99,
                "quantity": 8,
                "category": "Electronics"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedProduct)
                .when()
                .put("/api/products/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Laptop"))
                .body("price", equalTo(1299.99f));
    }

    @Test
    @Order(8)
    public void testUpdateProductNotFound() {
        String updatedProduct = """
            {
                "name": "Updated Product",
                "price": 99.99,
                "quantity": 5
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedProduct)
                .when()
                .put("/api/products/999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    public void testDeleteProduct() {
        given()
                .when()
                .delete("/api/products/2")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    public void testDeleteProductNotFound() {
        given()
                .when()
                .delete("/api/products/999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(11)
    public void testFilterByCategory() {
        given()
                .queryParam("category", "Electronics")
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("category", everyItem(equalTo("Electronics")));
    }

    @Test
    @Order(12)
    public void testNotFoundRoute() {
        given()
                .when()
                .get("/api/nonexistent")
                .then()
                .statusCode(404)
                .body("message", equalTo("Route not found"));
    }
}