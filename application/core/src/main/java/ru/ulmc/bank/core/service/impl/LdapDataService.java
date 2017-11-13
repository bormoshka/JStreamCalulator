package ru.ulmc.bank.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Controller;
import ru.ulmc.bank.pojo.ldap.LdapAttribute;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Контроллер аутентификации
 */
@Controller
@Profile("ldap")
public class LdapDataService {
    private static final Logger LOG = LoggerFactory.getLogger(LdapDataService.class);
    private final LdapTemplate ldapTemplate;

    @Value("${auth.ldap.userDnPatterns}")
    private String userDnPatterns;

    @Value("${auth.ldap.baseDn}")
    private String ldapBaseDn;

    @Value("${auth.ldap.usersBase}")
    private String usersBase;

    @Autowired
    public LdapDataService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public List<String> getAllPersonNames() {
        return ldapTemplate.search(
                query().where("objectclass").is("person"),
                (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());
    }

    public Collection<LdapAttribute> getUserAttributes(String login) {
        DirContextOperations context = ldapTemplate.searchForContext(
                query().countLimit(1).base(usersBase)
                        .filter("uid={0}", login));
        Attributes attrs = context.getAttributes();
        NamingEnumeration<String> ids = context.getAttributes().getIDs();
        List<LdapAttribute> list = new ArrayList<>();
        try {
            while (ids.hasMore()) {
                String id = ids.next();
                Object value = attrs.get(id).get();
                if (value instanceof String) {
                    list.add(new LdapAttribute(id, (String) value));
                }
            }
        } catch (NamingException e) {
            LOG.error("Ошибка чтения из LDAP", e);
        }

        return list;
    }
}
