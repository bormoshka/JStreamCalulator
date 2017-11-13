package ru.ulmc.bank.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.vaadin.spring.security.annotation.EnableVaadinSharedSecurity;

import java.util.Collection;

import ru.ulmc.bank.core.service.impl.UserServiceImpl;

/**
 * Настройки авторизации/аутентификации
 */
@EnableWebSecurity
@EnableVaadinSharedSecurity
@Profile("db-auth")
public class WebSecurityConfigDb extends WebSecurityConfigCommon {

    @Value("${auth.provider}")
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
