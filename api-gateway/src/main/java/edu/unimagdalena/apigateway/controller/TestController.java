package edu.unimagdalena.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para probar timeouts
 * Simula delays para verificar que CircuitBreaker + Timeout funcionan
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/delay/{seconds}")
    public ResponseEntity<Map<String, Object>> delay(@PathVariable int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Response after " + seconds + " seconds");
        response.put("timestamp", LocalDateTime.now());
        response.put("delayed", seconds + "s");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/fast")
    public ResponseEntity<Map<String, Object>> fast() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Fast response");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}