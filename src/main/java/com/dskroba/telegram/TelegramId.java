package com.dskroba.telegram;

import java.util.Objects;

public record TelegramId(
        Long telegramId,
        String telegramHandle,
        Long chatId
) {
    public boolean hasSameMetadata(TelegramId other) {
        if (other == null) return false;
        return Objects.equals(this.telegramHandle, other.telegramHandle)
                && Objects.equals(this.chatId, other.chatId);
    }
}
