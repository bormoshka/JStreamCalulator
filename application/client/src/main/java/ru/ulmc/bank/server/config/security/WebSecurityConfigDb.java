package ru.ulmc.bank.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.spring.security.annotation.EnableVaadinSharedSecurity;
import ru.ulmc.bank.core.service.impl.UserServiceImpl;

/**
 * Настройки авторизации/аутентификации
 */
@EnableWebSecurity
@EnableVaadinSharedSecurity
@Profile("db-auth")
@Order(200)
public class WebSecurityConfigDb extends WebSecurityConfigCommon {

    @Value("${auth.provider:db}")
    private String authProvider;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                UserServiceImpl userService,
                                PasswordEncoder passwordEncoder) throws Exception {
        if (AuthProvider.DB.name().equalsIgnoreCase(authProvider)) {
            auth
                    .userDetailsService(userService)
                    .passwordEncoder(passwordEncoder);
        }
    }
}
