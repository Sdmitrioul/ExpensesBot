package com.dskroba.services;

import com.dskroba.google.GoogleTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleSheetFacade {
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
        //TODO
        return true;
    }
}
