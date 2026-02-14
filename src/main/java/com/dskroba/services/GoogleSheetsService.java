package com.dskroba.services;

import com.dskroba.base.exception.InitializationException;
import com.dskroba.configurations.properties.GoogleProperties;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleSheetsService {
    private static final Logger log = LogManager.getLogger(GoogleSheetsService.class);

    private static final String USER_ID = "user";

    private final GoogleAuthorizationCodeFlow flow;
    private final NetHttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final String applicationName;

    private volatile Sheets sheetsInstance;

    @Autowired
    public GoogleSheetsService(
            GoogleAuthorizationCodeFlow flow,
            NetHttpTransport httpTransport,
            JsonFactory jsonFactory,
            GoogleProperties properties) {
        this.flow = flow;
        this.httpTransport = httpTransport;
        this.jsonFactory = jsonFactory;
        this.applicationName = properties.applicationName();

        // Try to initialize from stored credentials
        tryInitializeFromStoredCredentials();
    }

    private void tryInitializeFromStoredCredentials() {
        try {
            Credential credential = flow.loadCredential(USER_ID);
            if (credential != null && isCredentialValid(credential)) {
                this.sheetsInstance = buildSheetsService(credential);
                log.info("Sheets service initialized from stored credentials");
            }
        } catch (IOException e) {
            log.error("Could not load stored credentials", e);
        }
    }

    private boolean isCredentialValid(Credential credential) {
        return credential.getRefreshToken() != null ||
                credential.getExpiresInSeconds() == null ||
                credential.getExpiresInSeconds() > 60;
    }

    private Sheets buildSheetsService(Credential credential) {
        return new Sheets.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }

    public boolean isAuthorized() {
        return sheetsInstance != null;
    }

    public Sheets getSheets() {
        if (sheetsInstance == null) {
            throw new InitializationException("Google Sheets not authorized. Please complete OAuth flow first.");
        }
        return sheetsInstance;
    }

    public void completeAuthorization(String code, String redirectUri) throws IOException {
        var storage = flow.createAndStoreCredential(
                flow.newTokenRequest(code).setRedirectUri(redirectUri).execute(),
                USER_ID);

        this.sheetsInstance = buildSheetsService(storage);

        log.info("Google Sheets authorization completed successfully");
    }

    public GoogleAuthorizationCodeFlow getFlow() {
        return flow;
    }
}