package com.dskroba.controller;

import com.dskroba.configurations.properties.GoogleProperties;
import com.dskroba.services.GoogleSheetsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {
    private static final Logger log = LogManager.getLogger(GoogleAuthController.class);

    private final GoogleSheetsService googleSheetsService;
    private final String redirectUri;

    public GoogleAuthController(
            GoogleSheetsService googleSheetsService,
            GoogleProperties properties) {
        this.googleSheetsService = googleSheetsService;
        this.redirectUri = properties.redirectUri();
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        boolean authorized = googleSheetsService.isAuthorized();
        return ResponseEntity.ok(Map.of(
                "authorized", authorized,
                "message", authorized ? "Ready to use Google Sheets" : "Authorization required"
        ));
    }

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getAuthUrl() {
        if (googleSheetsService.isAuthorized()) {
            return ResponseEntity.ok(Map.of(
                    "message", "Already authorized",
                    "status", "ready"
            ));
        }

        String authUrl = googleSheetsService.getFlow()
                .newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build();

        log.info("Authorization URL generated: {}", authUrl);

        return ResponseEntity.ok(Map.of(
                "url", authUrl,
                "instructions", "Open this URL in any browser to authorize"
        ));
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            log.error("OAuth error: {}", error);
            return ResponseEntity.badRequest()
                    .body("<html><body><h1>Authorization Failed</h1><p>Error: " + error + "</p></body></html>");
        }

        if (code == null) {
            return ResponseEntity.badRequest()
                    .body("<html><body><h1>Missing Code</h1><p>No authorization code received.</p></body></html>");
        }

        try {
            googleSheetsService.completeAuthorization(code, redirectUri);
            log.info("Authorization completed successfully");

            return ResponseEntity.ok(
                    "<html><body>" +
                            "<h1>Authorization Successful!</h1>" +
                            "<p>You can close this window and return to the application.</p>" +
                            "</body></html>"
            );
        } catch (IOException e) {
            log.error("Failed to complete authorization", e);
            return ResponseEntity.internalServerError()
                    .body("<html><body><h1>Authorization Failed</h1><p>" + e.getMessage() + "</p></body></html>");
        }
    }
}