package de.operator.bot.service.application.enums;

public enum ServiceButton {
    MAIN_MENU("MAIN_MENU"),
    MY_TASKS("MY_TASKS"),
    MY_COMPLETED_TASKS("MY_COMPLETED_TASKS"),
    AVAILABLE_TASKS("AVAILABLE_TASKS"),
    MY_TASK("MY_TASK"),
    MY_COMPLETED_TASK("MY_COMPLETED_TASK"),
    AVAILABLE_TASK("AVAILABLE_TASK"),
    INFO("INFO"),
    INFOS("INFOS"),
    ACCEPT_TASK("ACCEPT_TASK"),
    GIVE_ANSWER("GIVE_ANSWER");

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
