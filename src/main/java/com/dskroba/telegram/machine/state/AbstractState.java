package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;
import com.dskroba.telegram.machine.StateMachineException;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractState implements State {
    private final String name;
    protected final UserContext context;

    protected AbstractState(String name, UserContext context) {
        this.name = name;
        this.context = context;
    }

    @Override
    public State applyAction(Action action) {
        if (!getAllowedActions().stream().map(a -> action.getDescription().equals(a.getDescription()))
                .reduce(false, Boolean::logicalOr)) {
            throw new StateMachineException(
                    "Not supported transition from " + this + " by action \"" + action.getDescription() + "\"");
        }
        return action.execute(this);
    }

    @Override
    public void initState() throws StateMachineException {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardMarkup(this.getAllowedActions());
        context.makeResponse(this.message().orElse("What do you want me to do?"),
                message -> message.replyMarkup(replyKeyboardMarkup));
    }

    protected ReplyKeyboardMarkup keyboardMarkup(List<Action> actions) {
        List<Action> sortedActions = actions.stream()
                .filter(Predicate.not(Action::isHidden))
                .sorted(Comparator.comparingInt(Action::priority))
                .toList();
        List<KeyboardButton[]> buttons = new ArrayList<>();
        Queue<KeyboardButton> queue = new LinkedList<>();
        for (Action action : sortedActions) {
            if (action.isFullRow()) {
                buttons.add(queue.toArray(new KeyboardButton[0]));
                queue.clear();
                buttons.add(new KeyboardButton[]{new KeyboardButton(action.getDescription())});
                continue;
            } else if (queue.size() == 3) {
                buttons.add(queue.toArray(new KeyboardButton[0]));
                queue.clear();
            }
            queue.add(new KeyboardButton(action.getDescription()));
        }
        if (!queue.isEmpty()) {
            buttons.add(queue.toArray(new KeyboardButton[0]));
        }
        KeyboardButton[][] buttonArray = new KeyboardButton[buttons.size()][];
        for (int i = 0; i < buttonArray.length; i++) {
            buttonArray[i] = buttons.get(i);
        }
        return new ReplyKeyboardMarkup(buttonArray)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
    }

    @Override
    public String toString() {
        return name;
    }
}
