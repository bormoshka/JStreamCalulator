package ru.ulmc.bank.ui.event;

import lombok.Getter;

/**
 * Event bus events used in Dashboard are listed here as inner classes.
 */
public abstract class UiEvents {
    private UiEvents() {
    }

    public static final class UserLoginRequestedEvent {
        @Getter
        private final String userName, password;

        public UserLoginRequestedEvent(final String userName,
                                       final String password) {
            this.userName = userName;
            this.password = password;
        }

    }

    public static final class UserLoginResponseEvent {
    }

    public static class BrowserResizeEvent {

    }

    public static class UserLoggedOutEvent {

    }

    public static class NotificationsCountUpdatedEvent {
    }

    public static final class ReportsCountUpdatedEvent {
        @Getter
        private final int count;

        public ReportsCountUpdatedEvent(final int count) {
            this.count = count;
        }
    }

    public static class CloseOpenWindowsEvent {
    }

    public static class ProfileUpdatedEvent {
    }
}
