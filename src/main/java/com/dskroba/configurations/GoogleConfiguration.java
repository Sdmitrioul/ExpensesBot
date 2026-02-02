package com.dskroba.configurations;

import com.dskroba.base.exception.InitializationException;
import com.dskroba.configurations.properties.GoogleProperties;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties({GoogleProperties.class})
public class GoogleConfiguration {
    private static final Logger log = LogManager.getLogger(GoogleConfiguration.class);

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    @Bean
    public Sheets sheetsService(GoogleProperties properties) {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            return new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport, properties))
                    .setApplicationName(properties.applicationName())
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Unable to create google sheet service.", e);
            throw new InitializationException("Unable to create google sheet service.", e);
        }
    }

    private static Credential getCredentials(final NetHttpTransport transport, GoogleProperties properties) {
        try {
            GoogleAuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow(transport, properties);
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException e) {
            log.error("Error during authorizing user for installed scope", e);
            throw new InitializationException("Error during authorizing user for installed scope", e);
        }
    }

    private static GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(
            NetHttpTransport transport,
            GoogleProperties properties) {
        try {
            return new GoogleAuthorizationCodeFlow.Builder(
                    transport, JSON_FACTORY, credentials(properties.credentialsFile()), SCOPES)
                    .setDataStoreFactory(tokenStorage(properties.tokenStoragePath()))
                    .setAccessType("offline")
                    .build();
        } catch (IOException e) {
            log.error("Problem setting token storage", e);
            throw new InitializationException("Problem setting token storage", e);
        }
    }

    private static FileDataStoreFactory tokenStorage(String path) {
        try {
            return new FileDataStoreFactory(new File(path));
        } catch (IOException e) {
            log.error("Problem creating storage file for token, path: {}", path);
            throw new InitializationException("Problem creating storage file for token, path: " + path, e);
        }
    }

    private static GoogleClientSecrets credentials(String credentialsFile) {
        try {
            return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(
                    new FileInputStream(credentialsFile), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            log.error("Credentials not found, file: {}", credentialsFile);
            throw new InitializationException("Credentials not found, file: " + credentialsFile, e);
        } catch (IOException e) {
            log.error("Problem reading credentials file: {}", credentialsFile);
            throw new InitializationException("Problem reading credentials file: " + credentialsFile, e);
        }
    }

}
