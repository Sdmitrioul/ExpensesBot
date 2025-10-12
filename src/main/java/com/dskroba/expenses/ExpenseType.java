package com.dskroba.expenses;

public enum ExpenseType {
    BILLS("#FF5722"),
    FOOD("#4CAF50"),
    TRANSPORT("#2196F3"),
    HEALTHCARE("#E91E63"),
    ANIMAL("#FF9800"),
    ENTERTAINMENT("#9C27B0"),
    CAFE("#009688"),
    CLOTHES("#795548"),
    BEAUTY("#00BCD4"),
    HOME("#FFC107"),
    SPORT("#FF6B35"),
    SAVINGS("#8BC34A"),
    DEBT("#F44336"),
    INSURANCE("#3F51B5"),
    MISCELLANEOUS("#607D8B");

    private final String color;

    ExpenseType(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String optionName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}