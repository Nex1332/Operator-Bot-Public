package de.operator.bot.service.enums;

public enum ServiceButton {
    PAY("PAY"),
    MAKE_ORDER("MAKE_ORDER"),
    MAKE_DOCUMENT_ORDER("MAKE_DOCUMENT_ORDER"),
    MAKE_TERMIN("MAKE_TERMIN"),
    ALL_MY_ORDERS("ALL_MY_ORDERS"),
    CONFIRMED_ORDERS("CONFIRMED_ORDERS"),
    UNCONFIRMED_ORDERS("UNCONFIRMED_ORDERS"),
    ADD_OR_CHANGE_INFO("ADD_OR_CHANGE_INFO"),
    CANCEL_ORDER("CANCEL_ORDER"),
    REPORT_A_PROBLEM("REPORT_A_PROBLEM"),
    ACCEPT_COMPLETED_TASK("ACCEPT_COMPLETED_TASK"),
    REQUEST_FREE_ORDER("REQUEST_FREE_ORDER"),
    REQUEST_REFUND("REQUEST_REFUND"),
    ACCEPT_REQUEST_REFUND("ACCEPT_REQUEST_REFUND"),
    USE_FREE_ORDER("USE_FREE_ORDER"),
    BACK_TO_MENU("BACK_TO_MENU");

    private final String value;

    ServiceButton(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceButton fromValue(String v) {
        for (ServiceButton c : ServiceButton.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
