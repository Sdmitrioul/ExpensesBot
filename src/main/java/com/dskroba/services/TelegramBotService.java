package com.dskroba.services;

import com.dskroba.base.bean.AbstractBean;
import com.dskroba.statemachine.AuthorizationException;
import com.dskroba.statemachine.StateMachine;
import com.dskroba.statemachine.state.CommandStates;
import com.dskroba.telegram.TelegramExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dskroba.base.Utils.shutdownExecutorService;
import static com.dskroba.telegram.TelegramUtils.chatId;
import static com.dskroba.telegram.TelegramUtils.user;

public class TelegramBotService extends AbstractBean implements UpdatesListener {
    private static final Logger log = LogManager.getLogger(TelegramBotService.class);

    private final TelegramBot bot;
    private final StateMachine<Update> stateMachine;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public TelegramBotService(TelegramBot bot, StateMachine<Update> stateMachine) {
        this.bot = bot;
        this.stateMachine = stateMachine;
    }

    @Override
    public void startImpl() {
        BotCommand[] commands = Arrays.stream(CommandStates.values())
                .map(commandState -> new BotCommand(commandState.code(), commandState.getDescription()))
                .toArray(BotCommand[]::new);
        bot.execute(new SetMyCommands(commands));
        bot.setUpdatesListener(this, new TelegramExceptionHandler());
    }

    @Override
    public void stopImpl() {
        bot.removeGetUpdatesListener();
    }

    @Override
    public int process(List<Update> updates) {
        executorService.execute(() -> updates.forEach(update -> {
            try {
                stateMachine.processEvent(update);
            } catch (AuthorizationException e) {
                processAuthorizationException(update, e);
            }
        }));
        return CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void close() {
        bot.shutdown();
        shutdownExecutorService(executorService);
    }

    private void processAuthorizationException(Update update, AuthorizationException e) {
        log.error("User without access, tried to use bot: {}", user(update), e);
        Long chatId = chatId(update);
        if (chatId != null) {
            bot.execute(new SendMessage(chatId, "Sorry, you don't have access to this bot."));
        }
    }
}
