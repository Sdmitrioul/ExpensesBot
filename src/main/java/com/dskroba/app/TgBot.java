package com.dskroba.app;

import com.dskroba.base.Configuration;
import com.dskroba.base.Properties;
import com.dskroba.notion.NotionContext;
import com.dskroba.telegram.TelegramContext;
import com.dskroba.telegram.TelegramExceptionHandler;
import com.dskroba.telegram.TelegramUpdatesListener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.dskroba.base.Properties.Property.TELEGRAM_ALLOWED_USERS;
import static com.dskroba.base.Properties.Property.TELEGRAM_BOT_TOKEN;

public class TgBot {
    private static final Logger LOGGER = LogManager.getLogger(TgBot.class);

    public static void main(String[] args) throws Exception {
        Properties globalProperties = Configuration.getGlobalProperties();
        String token = globalProperties.get(TELEGRAM_BOT_TOKEN);
        LOGGER.info("Telegram bot token: " + token);

        TelegramBot bot = new TelegramBot(token);
        TelegramContext context = new TelegramContext(token, Arrays.stream(globalProperties.get(TELEGRAM_ALLOWED_USERS).split(";"))
                .collect(Collectors.toSet()), bot, Clock.systemDefaultZone());
        bot.execute(new SetMyCommands(
                new BotCommand("expenses", "show expenses"),
                new BotCommand("add", "Add expense")
        ));
        try (NotionContext notionContext = new NotionContext(Clock.systemDefaultZone());
             TelegramUpdatesListener listener = new TelegramUpdatesListener(bot, context, notionContext.getFacade())) {
            bot.setUpdatesListener(listener, new TelegramExceptionHandler());
            while (true) {
                //Do nothing
            }
        }
    }
}
