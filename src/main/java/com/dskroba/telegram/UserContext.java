package com.dskroba.telegram;

import com.dskroba.base.Configuration;
import com.dskroba.base.Properties;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;

import java.time.Clock;
import java.time.ZoneId;
import java.util.concurrent.ExecutorService;

public class UserContext {
    private final TelegramBot bot;
    private final Long chatId;
    private final Clock clock;
    private final User user;
    private final ExecutorService executorService;

    public UserContext(TelegramBot bot, Long chatId, User user, ExecutorService executorService) {
        this.bot = bot;
        this.chatId = chatId;
        this.executorService = executorService;
        //TODO: get timezone from user
        this.clock = Clock
                .system(ZoneId.of(Configuration.getGlobalProperties()
                        .get(Properties.Property.APPLICATION_TIME_ZONE)));
        this.user = user;
    }

    public TelegramBot getBot() {
        return bot;
    }

    public Long getChatId() {
        return chatId;
    }

    public Clock getClock() {
        return clock;
    }

    public User getUser() {
        return user;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
