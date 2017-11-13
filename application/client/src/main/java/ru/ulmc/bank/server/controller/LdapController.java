package ru.ulmc.bank.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import ru.ulmc.bank.core.service.impl.LdapDataService;
import ru.ulmc.bank.pojo.ldap.LdapAttribute;

import java.util.Collection;
import java.util.Collections;

@Controller
@Profile("ldap")
public class LdapController {
    private static final Logger LOG = LoggerFactory.getLogger(LdapController.class);
    private final LdapDataService service;

    @Autowired
    public LdapController(LdapDataService service) {
        this.service = service;
    }

    public Collection<LdapAttribute> getUserAttributes(String userLogin) {
        try {
            Collection<LdapAttribute> result = service.getUserAttributes(userLogin);
            if (result == null) {
                return Collections.EMPTY_LIST;
            }
            return result;
        } catch (Exception ex) {
            LOG.error("Ошибка обращения к LDAP", ex);
            return Collections.EMPTY_LIST;
        }
    }
}
