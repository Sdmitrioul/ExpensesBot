package com.dskroba.telegram;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TelegramUpdatesListener implements UpdatesListener {
    private static final Logger LOGGER = LogManager.getLogger(TelegramUpdatesListener.class);
    private final TelegramContext context;

    public TelegramUpdatesListener(TelegramContext context) {
        this.context = context;
    }

    @Override
    public int process(List<Update> list) {
        list.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        LOGGER.debug("Processing update {}", update);
        Message message = update.message();
        if (message == null || message.chat() == null) return;
        LOGGER.trace("Update text is {} ", message.text());
        UserContext userContext = context.getUserContext(update);
        if (userContext == null) {
            LOGGER.trace("User can't be verified, update is skipped {}", update);
            context.processUpdate(() ->
                    context.sendResponse(message, "We are sorry, bot is not public, you can't use it!"));
            return;
        }
        context.processUpdate(() -> userContext.executeUpdate(update));
    }
}
