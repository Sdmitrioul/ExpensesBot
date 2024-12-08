package com.dskroba.telegram;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.response.BaseResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TelegramExceptionHandler implements ExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(TelegramExceptionHandler.class);

    @Override
    public void onException(TelegramException e) {
        if (e.response() != null) {
            BaseResponse response = e.response();
            LOGGER.error("Gor error from telegram!");
            LOGGER.error("Error code: {}, description {}", response.errorCode(), response.description());
            return;
        }
        LOGGER.error("Network error: {}", e.getMessage());
    }
}
