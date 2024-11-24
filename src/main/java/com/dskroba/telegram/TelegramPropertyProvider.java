package com.dskroba.telegram;

public class TelegramPropertyProvider {
    private final String telegramToken;

    public TelegramPropertyProvider(String telegramToken) {
        this.telegramToken = telegramToken;
    }

    public String getTelegramToken() {
        return telegramToken;
    }
}
