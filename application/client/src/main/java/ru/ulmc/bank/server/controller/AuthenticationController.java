package ru.ulmc.bank.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import ru.ulmc.bank.core.common.exception.AuthenticationException;
import ru.ulmc.bank.core.common.security.UserPrincipal;
import ru.ulmc.bank.core.service.impl.UserServiceImpl;
import ru.ulmc.bank.entities.persistent.system.User;
import ru.ulmc.bank.dao.repository.UserRoleRepository;
import ru.ulmc.bank.server.config.security.AuthProvider;
import ru.ulmc.bank.server.config.security.CurrentAuthenticationProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * Контроллер аутентификации
 */
@Controller
public class AuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;

    private final UserServiceImpl userDetailsService;
    private final UserRoleRepository roleRepository;

    @Value("${auth.provider}")
    private String authProvider;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserServiceImpl userDetailsService,
                                    UserRoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.roleRepository = roleRepository;
    }

    /**
     * Аутентифицирует пользователя по логину и паролю.
     *
     * @param login       Имя пользователя в системе
     * @param password    пароль
     * @param httpRequest объект запроса
     * @return сущность пользователя
     * @throws AuthenticationException если пользователь не найден или не соответствует пара логин/пароль
     */
    public User authenticate(String login, String password, HttpServletRequest httpRequest) throws AuthenticationException {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);
            token.setDetails(new WebAuthenticationDetails(httpRequest));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (AuthProvider.LDAP.name().equalsIgnoreCase(authProvider)) {
                User user = userDetailsService.findUser(((LdapUserDetailsImpl) authentication.getPrincipal()).getUsername());
                LOG.debug("User frol LDAP logged In with login: {}, roles empty: {}", user.getLogin(),
                        user.getRoles().isEmpty());
                return user;
            } else {
                return ((UserPrincipal) authentication.getPrincipal()).getUser();
            }
        } catch (Exception ex) {
            LOG.error("Auth error", ex);
            throw new AuthenticationException("Ошибка аутентификации!", ex);
        }
    }
}
