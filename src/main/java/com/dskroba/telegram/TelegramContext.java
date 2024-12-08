package com.dskroba.telegram;

import com.pengrad.telegrambot.TelegramBot;

import java.time.Clock;
import java.util.Set;

public class TelegramContext {
    private final String telegramToken;
    private final Set<String> allowedUsers;
    private final TelegramBot telegramBot;
    private final Clock clock;

    public TelegramContext(String telegramToken, Set<String> allowedUsers, TelegramBot telegramBot, Clock clock) {
        this.telegramToken = telegramToken;
        this.telegramBot = telegramBot;
        this.clock = clock;
        this.allowedUsers = allowedUsers;
    }

    public String getTelegramToken() {
        return telegramToken;
    }

    public boolean isAllowedUser(String user) {
        return allowedUsers.contains(user);
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }

    public Clock getClock() {
        return clock;
    }
}
