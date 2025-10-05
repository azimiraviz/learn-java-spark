package com.learning.java.spark;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for Hello World application
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HelloWorldTest {

    @BeforeAll
    public static void setup() {
        // Start the application in a separate thread
        new Thread(() -> HelloWorld.main(null)).start();

        // Wait for the server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Configure RestAssured
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    @Order(1)
    @DisplayName("Test root endpoint returns plain text greeting")
    public void testRootEndpoint() {
        given()
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .contentType("text/plain")
                .body(containsString("Hello, Spark Java!"));
    }

    @Test
    @Order(2)
    @DisplayName("Test HTML endpoint returns HTML content")
    public void testHtmlEndpoint() {
        given()
                .when()
                .get("/html")
                .then()
                .statusCode(200)
                .contentType("text/html")
                .body(containsString("<h1>"))
                .body(containsString("Hello from Spark!"));
    }

    @Test
    @Order(3)
    @DisplayName("Test JSON endpoint returns valid JSON")
    public void testJsonEndpoint() {
        given()
                .when()
                .get("/json")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("message", equalTo("Hello, JSON!"))
                .body("status", equalTo("success"))
                .body("timestamp", notNullValue());
    }

    @Test
    @Order(4)
    @DisplayName("Test route parameter with name")
    public void testRouteParameter() {
        given()
                .when()
                .get("/hello/John")
                .then()
                .statusCode(200)
                .body(containsString("Hello, John"));
    }

    @Test
    @Order(5)
    @DisplayName("Test multiple route parameters")
    public void testMultipleRouteParameters() {
        given()
                .when()
                .get("/greet/Alice/25")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("greeting", equalTo("Hello, Alice"))
                .body("age", equalTo("25"));
    }

    @Test
    @Order(6)
    @DisplayName("Test query parameters")
    public void testQueryParameters() {
        given()
                .queryParam("q", "spark")
                .queryParam("filter", "java")
                .when()
                .get("/search")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("query", equalTo("spark"))
                .body("filter", equalTo("java"));
    }

    @Test
    @Order(7)
    @DisplayName("Test POST echo endpoint")
    public void testPostEcho() {
        String requestBody = "{\"name\":\"test\",\"value\":123}";

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/echo")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("method", equalTo("POST"))
                .body("received", equalTo(requestBody));
    }

    @Test
    @Order(8)
    @DisplayName("Test PUT update endpoint")
    public void testPutUpdate() {
        String updateData = "{\"field\":\"updated value\"}";

        given()
                .contentType("application/json")
                .body(updateData)
                .when()
                .put("/update/42")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo("42"))
                .body("action", equalTo("updated"));
    }

    @Test
    @Order(9)
    @DisplayName("Test DELETE endpoint")
    public void testDelete() {
        given()
                .when()
                .delete("/delete/99")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo("99"))
                .body("action", equalTo("deleted"))
                .body("success", equalTo(true));
    }

    @Test
    @Order(10)
    @DisplayName("Test custom status code")
    public void testCustomStatusCode() {
        given()
                .when()
                .get("/status/201")
                .then()
                .statusCode(201)
                .body("statusCode", equalTo(201));
    }

    @Test
    @Order(11)
    @DisplayName("Test info endpoint returns route information")
    public void testInfoEndpoint() {
        given()
                .when()
                .get("/info")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("name", equalTo("Hello World API"))
                .body("version", equalTo("1.0"))
                .body("routes", notNullValue());
    }

    @Test
    @Order(12)
    @DisplayName("Test 404 Not Found for invalid route")
    public void testNotFound() {
        given()
                .when()
                .get("/nonexistent")
                .then()
                .statusCode(404)
                .contentType("application/json")
                .body("status", equalTo(404))
                .body("message", containsString("not found"));
    }
}