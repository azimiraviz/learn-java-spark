package com.learning.java.spark.hello_world;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class HelloWorldApplication {
    private static final Logger log = LoggerFactory.getLogger(HelloWorldApplication.class);
    private static final Gson gson = new Gson();
    private static final int portNumber = 8080;

    public static void main(String[] args) {

        // Configure port (default 4567)
        port(portNumber);

        // Enable CORS for development
        enableCORS();

        // Define routes
        defineRoutes();

        // Exception handling
        setupExceptionHandlers();

        log.info("Hello World Server started on http://localhost:{}", portNumber);
    }

    private static void defineRoutes() {

        // Simple GET - Plain text response
        get("/", (req, res) -> {
            res.type("text/plain");
            return "Hello, Spark Java!";
        });

        // GET with HTML response
        get("/html", (req, res) -> {
            res.type("text/html");
            return "<h1>Hello from Spark!</h1>" +
                    "<p>This is an HTML response</p>" +
                    "<a href='/'>Back to home</a>";
        });

        // GET with JSON response
        get("/json", (req, res) -> {
            res.type("application/json");
            Map<String, Object> data = new HashMap<>();
            data.put("message", "Hello, JSON!");
            data.put("status", "success");
            data.put("timestamp", System.currentTimeMillis());
            return gson.toJson(data);
        });

        // GET with route parameter
        get("/hello/:name", (req, res) -> {
            String name = req.params(":name");
            return "Hello, " + name + "! 👋";
        });

        // GET with multiple route parameters
        get("/greet/:name/:age", (req, res) -> {
            String name = req.params(":name");
            String age = req.params(":age");

            res.type("application/json");
            Map<String, String> response = new HashMap<>();
            response.put("greeting", "Hello, " + name);
            response.put("age", age);
            response.put("message", "Welcome to Spark Java!");

            return gson.toJson(response);
        });

        // GET with query parameters
        get("/search", (req, res) -> {
            String query = req.queryParams("q");
            String filter = req.queryParams("filter");

            res.type("application/json");
            Map<String, Object> response = new HashMap<>();
            response.put("query", query != null ? query : "none");
            response.put("filter", filter != null ? filter : "none");
            response.put("info", "Query params example: /search?q=spark&filter=java");

            return gson.toJson(response);
        });

        // POST example - Echo back JSON
        post("/echo", (req, res) -> {
            res.type("application/json");

            String body = req.body();
            Map<String, Object> response = new HashMap<>();
            response.put("received", body);
            response.put("contentType", req.contentType());
            response.put("method", "POST");

            return gson.toJson(response);
        });

        // PUT example
        put("/update/:id", (req, res) -> {
            String id = req.params(":id");
            String body = req.body();

            res.type("application/json");
            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("action", "updated");
            response.put("data", body);

            return gson.toJson(response);
        });

        // DELETE example
        delete("/delete/:id", (req, res) -> {
            String id = req.params(":id");

            res.type("application/json");
            res.status(200);

            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("action", "deleted");
            response.put("success", true);

            return gson.toJson(response);
        });

        // Route with custom status code
        get("/status/:code", (req, res) -> {
            int code = Integer.parseInt(req.params(":code"));
            res.status(code);
            res.type("application/json");

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", code);
            response.put("message", "Custom status code example");

            return gson.toJson(response);
        });

        // Info route - shows all available routes
        get("/info", (req, res) -> {
            res.type("application/json");
            Map<String, Object> info = new HashMap<>();
            info.put("name", "Hello World API");
            info.put("version", "1.0");
            info.put("routes", new String[]{
                    "GET  /                    - Plain text greeting",
                    "GET  /html                - HTML response",
                    "GET  /json                - JSON response",
                    "GET  /hello/:name         - Greeting with name parameter",
                    "GET  /greet/:name/:age    - Greeting with multiple parameters",
                    "GET  /search?q=&filter=   - Query parameters example",
                    "POST /echo                - Echo back request body",
                    "PUT  /update/:id          - Update example",
                    "DELETE /delete/:id        - Delete example",
                    "GET  /status/:code        - Custom status code",
                    "GET  /info                - This info page"
            });

            return gson.toJson(info);
        });
    }

    private static void enableCORS() {
        // Enable CORS for all routes
        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
    }

    private static void setupExceptionHandlers() {
        // Handle 404 - Not Found
        notFound((req, res) -> {
            res.type("application/json");
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("message", "Route not found: " + req.pathInfo());
            error.put("hint", "Try GET /info to see all available routes");
            return gson.toJson(error);
        });

        // Handle 500 - Internal Server Error
        internalServerError((req, res) -> {
            res.type("application/json");
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("message", "Internal server error");
            return gson.toJson(error);
        });

        // General exception handler
        exception(Exception.class, (e, req, res) -> {
            res.type("application/json");
            res.status(500);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("message", e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            res.body(gson.toJson(error));
        });
    }
}
