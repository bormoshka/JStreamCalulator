package ru.ulmc.bank.server.config.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Настройки авторизации/аутентификации
 */
public class WebSecurityConfigCommon extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/vaadinServlet/**", "/VAADIN/**", "/auth/**", "/*").permitAll()
                .antMatchers("/").anonymous()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .logout().permitAll();
    }

}
