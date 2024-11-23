package com.dskroba.type;

public enum ExpenseTag {
    GROCERIES("pink"),
    TRANSPORT("yellow"),
    DOG("green"),
    HOME("orange"),
    CAFE("default"),
    BILLS("red"),
    CLOTHES("gray"),
    SPORT("blue"),
    HEALTH("purple");

    private final String color;

    ExpenseTag(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
