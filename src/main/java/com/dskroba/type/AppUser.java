package com.dskroba.type;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record AppUser(Integer id, String name, Long chatId) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AppUser(Integer id1, String name1, Long chatId1))) return false;
        return Objects.equals(id, id1) && Objects.equals(name, name1) && Objects.equals(chatId, chatId1);
    }

    @NotNull
    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", chatId=" + chatId +
                '}';
    }

    public static class Builder {
        private Integer id;
        private String name;
        private Long chatId;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setChatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public AppUser build() {
            return new AppUser(id, name, chatId);
        }
    }
}
