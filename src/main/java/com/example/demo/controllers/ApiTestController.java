package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class ApiTestController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend API is running");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/cors")
    public Map<String, Object> corsTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS is working!");
        response.put("origin", "http://localhost:4200");
        return response;
    }
}