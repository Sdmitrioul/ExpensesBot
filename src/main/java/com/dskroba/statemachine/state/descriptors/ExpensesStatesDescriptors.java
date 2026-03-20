package com.dskroba.statemachine.state.descriptors;

public enum ExpensesStatesDescriptors implements StateDescriptor {
    CHOOSE_TAG,
    ADD_AMOUNT,
    ADD_DATE,
    ADD_DESCRIPTION,
    SAVE_EXPENSE;

    public static final String EXPENSE_ATTRIBUTE_NAME = "$expense_builder";
}
