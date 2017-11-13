package ru.ulmc.bank.ui.entity;

public enum RowStatus {
    NOT_CHANGED(""),
    CREATED("new-row"),
    EDITED("changed-row"),
    MARKED_FOR_DELETION("deletion-row");

    private String style;

    RowStatus(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }
}
