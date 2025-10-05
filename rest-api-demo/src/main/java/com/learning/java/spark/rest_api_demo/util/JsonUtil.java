package com.learning.java.spark.rest_api_demo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for JSON serialization/deserialization
 */
public class JsonUtil {
    private static final Gson gson;

    static {
        // Configure Gson with custom adapters for LocalDateTime
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                                context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .setPrettyPrinting()
                .create();
    }

    /**
     * Convert object to JSON string
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Convert JSON string to object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /**
     * Get Gson instance for advanced usage
     */
    public static Gson getGson() {
        return gson;
    }
}
