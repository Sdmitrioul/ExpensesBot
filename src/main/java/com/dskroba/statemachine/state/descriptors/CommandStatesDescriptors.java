package com.dskroba.statemachine.state.descriptors;

public enum CommandStatesDescriptors implements StateDescriptor {
    MENU("Provides bot menu."),
    IMPORT_DATA("Allow import expenses from csv file."),
    EXPORT_DATA("Allow export expenses."),
    SET_ACTIVE_CURRENCY("Allow setup of active account currency."),
    PROVIDE_LINK_TO_THE_DOCUMENT("Adds new google sheet document."),
    SELECT_ACTIVE_DOCUMENT("Allows select active google sheet document.");

    private final String description;

    CommandStatesDescriptors(String description) {
        this.description = description;
    }

    public String code() {
        return "/" + name().toLowerCase();
    }

    public String getDescription() {
        return description;
    }
}
