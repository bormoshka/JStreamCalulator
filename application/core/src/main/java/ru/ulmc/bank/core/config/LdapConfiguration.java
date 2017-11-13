package ru.ulmc.bank.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import java.util.Arrays;

@Configuration
@Profile("ldap")
public class LdapConfiguration {

    @Value("${auth.ldap.url}")
    private String ldapUrl;

    @Value("${auth.ldap.baseDn}")
    private String ldapBaseDn;

    @Value("${auth.ldap.admin.dn}")
    private String ldapAdminName;

    @Value("${auth.ldap.admin.password}")
    private String ldapAdminPassword;

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource source = new DefaultSpringSecurityContextSource(
                Arrays.asList(ldapUrl), ldapBaseDn);
        source.setAuthenticationStrategy(new SimpleDirContextAuthenticationStrategy());
        source.setUserDn(ldapAdminName);
        source.setPassword(ldapAdminPassword);
        return source;
    }
}