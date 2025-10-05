package com.dskroba.principals;

public record PrincipalKey(Long id, Long telegramId) {
    public static PrincipalKey resolved(Long id, Long telegramId) {
        return new PrincipalKey(id, telegramId);
    }

    public static PrincipalKey valueOf(Long telegramId) {
        return new PrincipalKey(null, telegramId);
    }

    public boolean isResolved() {
        return id != null;
    }
}
