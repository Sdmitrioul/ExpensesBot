package com.dskroba.services;

import com.dskroba.base.exception.CustomException;
import com.dskroba.google.GoogleTable;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GoogleSheetFacade {
    protected static final Logger log = LoggerFactory.getLogger(GoogleSheetFacade.class);
    private final GoogleSheetsService sheetsService;

    @Autowired
    public GoogleSheetFacade(GoogleSheetsService sheetsService) {
        this.sheetsService = sheetsService;
    }

    public boolean isReachableDocument(String link) {
        var documentId = GoogleTable.documentIdFromLink(link);
        if (documentId.isEmpty()) {
            return false;
        }
        Sheets sheets = sheetsService.getSheets();
        try {
            sheets.spreadsheets().get(documentId.get())
                    .setFields("spreadsheetId")
                    .execute();
            return true;
        } catch (GoogleJsonResponseException e) {
            log.warn("Document {} is not reachable: {} {}", documentId, e.getStatusCode(), e.getStatusMessage());
            return false;
        } catch (IOException e) {
            log.error("IO error while checking document {}", documentId, e);
            return false;
        }
    }

    /**
     * Ensures a sheet with the given name exists and has the expected header row.
     *
     * @param documentLink the Google Spreadsheet link
     * @param sheetName    the tab/sheet name to ensure
     * @param headerRow    the expected first row (e.g. List.of("Name", "Age", "Email"))
     * @return true if everything is OK (created or already correct), false on conflict
     */
    public boolean ensureSheetWithHeader(String documentLink, String sheetName, List<Object> headerRow) {
        Sheets sheets = sheetsService.getSheets();
        var spreadsheetId = GoogleTable.documentIdFromLink(documentLink)
                .orElseThrow(() -> new CustomException("Unexpected link exception"));
        try {
            Spreadsheet spreadsheet = sheets.spreadsheets().get(spreadsheetId)
                    .setFields("sheets.properties.title")
                    .execute();

            boolean sheetExists = spreadsheet.getSheets().stream()
                    .anyMatch(s -> sheetName.equals(s.getProperties().getTitle()));

            if (!sheetExists) {
                var addSheetRequest = new AddSheetRequest()
                        .setProperties(new SheetProperties().setTitle(sheetName));
                var batchRequest = new BatchUpdateSpreadsheetRequest()
                        .setRequests(List.of(new Request().setAddSheet(addSheetRequest)));
                sheets.spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute();

                writeHeaderRow(sheets, spreadsheetId, sheetName, headerRow);
                return true;
            }

            String range = sheetName + "!1:1";
            ValueRange result = sheets.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = result.getValues();

            if (values == null || values.isEmpty()) {
                writeHeaderRow(sheets, spreadsheetId, sheetName, headerRow);
                return true;
            }

            List<Object> existingRow = values.get(0);
            if (existingRow.equals(headerRow)) {
                log.info("Sheet '{}' already has the correct header row", sheetName);
                return true;
            }

            // 3c. First row is different — conflict
            log.error("Sheet '{}' has a conflicting header row. Expected: {}, Found: {}",
                    sheetName, headerRow, existingRow);
            return false;

        } catch (IOException e) {
            log.error("Failed to ensure sheet '{}' in spreadsheet {}", sheetName, spreadsheetId, e);
            throw new CustomException(e);
        }
    }

    private void writeHeaderRow(Sheets sheets, String spreadsheetId, String sheetName,
                                List<Object> headerRow) throws IOException {
        String range = sheetName + "!A1";
        ValueRange body = new ValueRange().setValues(List.of(headerRow));
        sheets.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
        log.info("Header row written to sheet '{}'", sheetName);
    }
}
