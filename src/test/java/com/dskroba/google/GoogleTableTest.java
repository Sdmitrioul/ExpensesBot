package com.dskroba.google;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleTableTest {
    @Test
    void basic() {
        assertEquals("1F5FQZwTSRsfsdfsdcSfZIcaKxgBmdrM", GoogleTable.documentIdFromLink("https://docs.google.com/spreadsheets/d/1F5FQZwTSRsfsdfsdcSfZIcaKxgBmdrM/edit?usp=sharing").get());
        assertTrue(GoogleTable.documentIdFromLink("https://docs.google.com/spreadsheets1F5FQZwTSRsfsdfsdcSfZIcaKxgBmdrM/edit?usp=sharing").isEmpty());
    }
}