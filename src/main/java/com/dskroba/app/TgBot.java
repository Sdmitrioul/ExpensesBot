package com.dskroba.app;

import com.dskroba.base.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.dskroba.base.Properties.Property.TELEGRAM_BOT_TOKEN;

public class TgBot {
    private static final Logger LOGGER = LogManager.getLogger(TgBot.class);
    public static void main(String[] args) {
        String token = Configuration.getGlobalProperties().get(TELEGRAM_BOT_TOKEN);
        LOGGER.info("Telegram bot token: " + token);
    }
}
