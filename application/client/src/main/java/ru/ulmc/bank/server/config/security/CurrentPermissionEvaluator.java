package ru.ulmc.bank.server.config.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by User on 06.04.2017.
 */
@Component
public class CurrentPermissionEvaluator implements PermissionEvaluator {

    public CurrentPermissionEvaluator() {
    }

    public boolean hasPermission(Authentication authentication, Object permission) {
        return hasPermission(authentication, null, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        } else {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals(permission))
                    return true;
            }
            return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new RuntimeException("Id-based permission evaluation not currently supported.");
    }
}
