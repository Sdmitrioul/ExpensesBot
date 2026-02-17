package com.dskroba.statemachine.state;

import com.dskroba.actor.Context;
import com.dskroba.google.GoogleTable;
import com.dskroba.principals.Principal;
import com.dskroba.type.Currency;

import java.util.List;

public final class ContextAccessor {
    public static Currency principalCurrency() {
        return principal().context().activeCurrency();
    }

    public static GoogleTable principalStorage() {
        return principal().context().activeTable();
    }

    public static List<GoogleTable> principalDocuments() {
        return principal().context().tables();
    }

    public static boolean isPrincipalInitiated() {
        return principal().context().activeTable() != null;
    }

    public static Long principalChatId() {
        return principal().key().chatId();
    }

    public static String principalTelegramHandle() {
        return principal().key().telegramHandle();
    }

    public static Principal principal() {
        return context().getPrincipal();
    }

    public static State getState() {
        return context().getState();
    }

    public static Context context() {
        return Context.PROVIDER.get();
    }

    private ContextAccessor() {
    }
}
