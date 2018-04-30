package ru.ulmc.bank.server.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Collections;

/**
 * Настройки авторизации/аутентификации
 */
public class WebSecurityConfigCommon extends WebSecurityConfigurerAdapter {


    @Bean
    public AuthenticationManager getAuthenticationManager(CurrentAuthenticationProvider ap){
        return new ProviderManager(Collections.singletonList(ap));
    }

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
