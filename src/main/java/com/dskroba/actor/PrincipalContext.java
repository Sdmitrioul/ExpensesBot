package com.dskroba.actor;

import com.dskroba.google.GoogleTable;
import com.dskroba.principals.Principal;
import com.dskroba.type.Currency;

import java.util.List;
import java.util.Objects;

public final class PrincipalContext {
    public static final ScopedValue<PrincipalContext> PROVIDER = ScopedValue.newInstance();

    public static Long principalId() {
        return PROVIDER.get().getPrincipal().key().telegramId();
    }

    public static Currency currency() {
        return PROVIDER.get().getPrincipal().context().activeCurrency();
    }

    public static boolean isInitiated() {
        return !PROVIDER.get().getPrincipal().context().tables().isEmpty();
    }

    public static GoogleTable table() {
        return PROVIDER.get().getPrincipal().context().activeTable();
    }

    public static List<GoogleTable> getAllTables() {
        return PROVIDER.get().getPrincipal().context().tables();
    }

    private volatile Principal principal;

    public PrincipalContext(Principal principal) {
        this.principal = principal;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PrincipalContext that)) return false;
        return Objects.equals(principal, that.principal);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(principal);
    }

    @Override
    public String toString() {
        return principal.toString();
    }
}
