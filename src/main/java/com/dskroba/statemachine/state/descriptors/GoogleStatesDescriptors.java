package com.dskroba.statemachine.state.descriptors;

public enum GoogleStatesDescriptors implements StateDescriptor {
    PROVIDE_SHEET_NAME,
    SAVE_GOOGLE_SHEET;
    public static final String SHEET_NAME_ATTRIBUTE = "$google_sheet_name_attribute";
    public static final String SHEET_DOCUMENT_LINK_ATTRIBUTE = "$google_sheet_document_link_attribute";
}
