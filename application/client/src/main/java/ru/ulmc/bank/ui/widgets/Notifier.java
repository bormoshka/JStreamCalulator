package ru.ulmc.bank.ui.widgets;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

import static ru.ulmc.bank.ui.common.CustomNotationConverter.toAlphaNumOnly;

@Slf4j
public class Notifier {
    private static SecureRandom rand = new SecureRandom();

    public static Notification humanized(String... msg) {
        Notification notification = new Notification(msg[0], Notification.Type.HUMANIZED_MESSAGE);
        if (msg.length > 1) {
            notification.setDescription(msg[1]);
        }
        notification.setDelayMsec(3500);
        notification.show(Page.getCurrent());
        return notification;
    }

    public static Notification errorHtml(String... msg) {
        Notification notification = new Notification(msg[0], Notification.Type.ERROR_MESSAGE);
        if (msg.length > 1) {
            notification.setDescription(msg[1]);
        }
        notification.setHtmlContentAllowed(true);
        notification.show(Page.getCurrent());
        return notification;
    }

    public static Notification logAndReportError(Throwable th, String... msg) {
        String exId = getErrorId();
        log.error("Caught exception with id " + exId, th);
        String errorMsg = (msg.length > 1 ? msg[0] + " | " + msg[1] : msg[0]) + " | Идентификатор ошибки: " + exId ;
        Page.getCurrent().getJavaScript().execute("console.log('" + errorMsg + "')");
        return Notifier.errorHtml( "[!]", errorMsg.replaceAll("\\|", "<br/>"));
    }

    public static Notification uncaughtException(Throwable th, String msg) {
        String exId = getErrorId();
        log.error("Unhandled exception with id " + exId, th);
        String errorMsg = "Произошла непредвиденная ошибка!<br/>" + msg + " <br/> Идентификатор ошибки: " + exId;
        Page.getCurrent().getJavaScript().execute("console.log('" + errorMsg + "')");
        return Notifier.errorHtml(":(", errorMsg);
    }

    private static String getErrorId() {
        return "EUID-".concat(toAlphaNumOnly(rand.nextInt()));
    }
}
