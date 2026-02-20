package com.educonnect.web.config;

import com.educonnect.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Public runtime config for the frontend (no auth). Used to avoid hardcoded API URL in the client.
 */
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class PublicConfigController {

    private final AppProperties appProperties;

    @GetMapping
    public ResponseEntity<Map<String, String>> getConfig() {
        return ResponseEntity.ok(Map.of(
                "apiUrl", appProperties.getApiPublicUrl() != null ? appProperties.getApiPublicUrl() : ""
        ));
    }
}
