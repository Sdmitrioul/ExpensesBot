package com.dskroba.telegram;

import com.dskroba.notion.NotionFacade;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;
import com.dskroba.telegram.machine.state.StateFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramUpdatesListener implements UpdatesListener, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(TelegramUpdatesListener.class);
    private final TelegramBot bot;
    private final TelegramContext context;
    private final NotionFacade notionFacade;
    private final Map<String, State> userStates = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public TelegramUpdatesListener(TelegramBot bot, TelegramContext context, NotionFacade notionFacade) {
        this.bot = bot;
        this.context = context;
        this.notionFacade = notionFacade;
    }

    @Override
    public int process(List<Update> list) {
        list.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        Message message = update.message();
        if (message == null) return;
        Long chatId = message.chat().id();
        if (!checkUser(message.from(), chatId)) {
            return;
        }
        LOGGER.info(message.text());
        String username = message.from().username();
        State currentState = userStates.get(username);
        if (currentState == null) {
            State startState = StateFactory.getStartState(new UserContext(bot, chatId, message.from(), executor), notionFacade);
            userStates.put(username, startState);
            extracted(chatId, startState);
            return;
        }
        Action action = currentState.parseAction(message.text());
        if (action == null) {
            extracted(chatId, currentState);
            return;
        }
        State newState = currentState.applyAction(action);
        userStates.put(username, newState);
        extracted(chatId, newState);
    }

    private void extracted(Long chatId, State newState) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                newState.getAllowedActions().stream().map(Action::getDescription).map(KeyboardButton::new).toList().toArray(new KeyboardButton[0]))
                .resizeKeyboard(true);
        SendMessage message = new SendMessage(chatId, "What do you want me to do?")
                .replyMarkup(replyKeyboardMarkup);

        executor.execute(() ->
                bot.execute(message));
    }

    private boolean checkUser(User from, Long chatId) {
        LOGGER.info("Checking user {}", from.username());
        if (!context.isAllowedUser(from.username())) {
            bot.execute(new SendMessage(chatId, "You are not allowed to use this bot"));
            return false;
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }
}
