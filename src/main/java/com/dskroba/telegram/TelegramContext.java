package com.dskroba.telegram;

import com.dskroba.repository.ExpenseRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.dskroba.base.Utils.shutdownExecutorService;
import static com.dskroba.telegram.TelegramUtils.chatId;
import static com.dskroba.telegram.TelegramUtils.user;

public class TelegramContext implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(TelegramContext.class);

    private final ExecutorService executors;
    private final Map<String, UserContext> users = new ConcurrentHashMap<>();
    private final TelegramBot telegramBot;
    private final ExpenseRepository expenseRepository;
    private final Set<String> allowedUsers;

    public TelegramContext(ExecutorService executors,
                           String telegramToken,
                           ExpenseRepository repository,
                           Set<String> allowedUsers) {
        this.executors = executors;
        this.telegramBot = new TelegramBot(telegramToken);
        this.expenseRepository = repository;
        this.allowedUsers = allowedUsers;
    }

    public void setListeners(TelegramUpdatesListener telegramUpdatesListener,
                             TelegramExceptionHandler telegramExceptionHandler) {
        telegramBot.setUpdatesListener(telegramUpdatesListener, telegramExceptionHandler);
    }


    public ExpenseRepository getExpenseRepository() {
        return expenseRepository;
    }

    public UserContext getUserContext(Update update) {
        return Optional.ofNullable(update.message())
                .map(Message::from)
                .map(User::username)
                .filter(allowedUsers::contains)
                .map(username -> users.computeIfAbsent(username,
                        key -> {
                            var userContext = new UserContext(chatId(update), user(update), this);
                            expenseRepository.addUser(userContext.getUserApp());
                            return userContext;
                        }))
                .orElse(null);
    }

    public void processUpdate(Runnable action) {
        executors.execute(action);
    }

    public void sendResponse(Message message, String response) {
        Long chatId = getChatId(message);
        if (chatId == null) {
            LOGGER.warn("Unable to get chat id from message: {} and send response: {}", message, response);
            return;
        }
        sendResponse(chatId, response, Function.identity());
    }

    public void sendResponse(Long chatId, String response, Function<SendMessage, SendMessage> messageTransformer) {
        telegramBot.execute(messageTransformer.apply(new SendMessage(chatId, response)));
        telegramBot.execute(new SendPhoto(chatId, "Type /help to see available commands"));
    }

    @Override
    public void close() {
        clearUserStates();
        telegramBot.shutdown();
        shutdownExecutorService(executors);
    }

    private void clearUserStates() {
        for (UserContext user : users.values()) {
            user.makeResponse("We unavailable for some time!", message -> message.replyMarkup(null));
        }
    }

    private static Long getChatId(Message message) {
        return Optional.ofNullable(message)
                .map(Message::chat)
                .map(Chat::id)
                .orElse(null);
    }
}
