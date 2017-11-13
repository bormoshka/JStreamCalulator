package ru.ulmc.bank.core.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ulmc.bank.entities.persistent.system.Permission;
import ru.ulmc.bank.entities.persistent.system.User;
import ru.ulmc.bank.entities.persistent.system.UserRole;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by User on 08.04.2017.
 */
public class UserPrincipal implements UserDetails {

    private final User user;
    private final Set<GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.user = user;
        authorities = new HashSet<>();
        for (UserRole role : user.getRoles()) {
            for (Permission perm : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(perm.getName()));
            }
        }
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
