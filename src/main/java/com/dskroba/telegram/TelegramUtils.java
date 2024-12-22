package com.dskroba.telegram;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;

import java.util.Optional;

public class TelegramUtils {
    public static User user(Update update) {
        return Optional.ofNullable(update)
                .map(Update::message)
                .map(Message::from)
                .orElse(null);
    }

    public static Long chatId(Update update) {
        return Optional.ofNullable(update)
                .map(Update::message)
                .map(Message::chat)
                .map(Chat::id)
                .orElse(null);
    }

    private TelegramUtils() {}
}
