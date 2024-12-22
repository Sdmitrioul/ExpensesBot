package com.dskroba.telegram;

import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;
import com.dskroba.telegram.machine.TryAgainAction;
import com.dskroba.telegram.machine.state.StateFactory;
import com.dskroba.type.DateFilter;
import com.dskroba.type.Expense;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class UserContext {
    private final static Logger LOGGER = LogManager.getLogger(UserContext.class);

    private final Clock clock;
    private final Long chatId;
    private final User user;
    private final TelegramContext context;
    private final AtomicReference<State> state = new AtomicReference<>();

    public UserContext(Long chatId, User user, Clock clock, TelegramContext context) {
        this.chatId = chatId;
        this.user = user;
        this.clock = clock;
        this.context = context;
        this.state.set(StateFactory.getStartState(this));
        initUser();
    }

    private void initUser() {
        state.set(StateFactory.getStartState(this));
        state.get().initState();
    }

    public Clock getClock() {
        return clock;
    }

    public void executeUpdate(Update update) {
        State current = requireNonNull(state.get(), "State cannot be null!");
        Action action = current.parseAction(update.message().text());
        if (action instanceof TryAgainAction tryAgainAction) {
            makeResponse(tryAgainAction.getMessage());
            return;
        } else if (action == null) {
            String allowedActionString = current.getAllowedActions().stream()
                    .map(Action::getDescription)
                    .collect(Collectors.joining(", ", "[", "]"));
            makeResponse("Can't execute update because the action is not allowed! " +
                    "Allowed actions: " + allowedActionString);
            return;
        }
        State newState = current.applyAction(action);
        if (!state.compareAndSet(current, newState)) {
            LOGGER.error("Can't execute update for user {}, due to state changed during update processing, " +
                            "prev {}, new expected {}",
                    user.id(), current, newState);
        } else {
            newState.initState();
        }
    }

    public void makeResponse(String text, Function<SendMessage, SendMessage> messageTransformer) {
        context.sendResponse(chatId, text, messageTransformer);
    }

    public void makeResponse(String text) {
        context.sendResponse(chatId, text, Function.identity());
    }

    public List<Expense> getUserExpenses(DateFilter dateFilter) {
        return context.getNotion()
                .getExpenses(dateFilter.getTimeIntervals(getClock()));
    }

    public void saveExpense(Expense expense) {
        context.processUpdate(() -> context.getNotion().insertExpense(expense));
    }
}
