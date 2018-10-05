package ru.ulmc.bank.server.config;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import lombok.Data;
import ru.ulmc.bank.entities.persistent.system.User;

/**
 * Хранит общие параметры сессии пользователя.
 */
@Getter @Setter @ToString @EqualsAndHashCodes
@Component
@Scope(scopeName = "vaadin-session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession implements Serializable {
    private User user = null;

    public boolean isAuthenticated() {
        return user != null;
    }
}
