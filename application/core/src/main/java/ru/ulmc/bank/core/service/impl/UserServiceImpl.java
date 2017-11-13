package ru.ulmc.bank.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ulmc.bank.core.common.security.UserPrincipal;
import ru.ulmc.bank.entities.persistent.system.Permission;
import ru.ulmc.bank.entities.persistent.system.User;
import ru.ulmc.bank.entities.persistent.system.UserRole;
import ru.ulmc.bank.dao.repository.UserRepository;
import ru.ulmc.bank.dao.repository.UserRoleRepository;

import java.util.*;


@Service
@Transactional
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("Не найден пользователь с именем " + login);
        }
        return new UserPrincipal(user);
    }

    public User findUser(String login) {
        return userRepository.findByLogin(login);
    }

    /**
     * Создает нового пользователя в системе или обновляет уже существующего.
     * использовать при работе с внешним источником аутентификационных данных.
     *
     * @param auth     Объект аутентификации пользователя LDAP
     * @param fullname полное имя пользователя
     */
    public void createOrUpdateUser(LdapUserDetailsImpl auth, String fullname) {
        User user = findUser(auth.getUsername());
        if (user == null) {
            user = new User();
            user.setLogin(auth.getUsername());
            user.setPassword(UUID.randomUUID().toString()); //not null поле, которое не спользуется
        }
        user.setEnabled(auth.isEnabled() && auth.isAccountNonLocked() && auth.isAccountNonExpired());
        user.setFullName(fullname);
        Set<UserRole> setOfRoles = new HashSet<>();
        auth.getAuthorities().forEach(grantedAuthority -> {
            UserRole role = roleRepository.findByName(grantedAuthority.getAuthority());
            if (role != null) {
                setOfRoles.add(role);
            }
        });
        user.setRoles(setOfRoles);
        userRepository.save(user);
    }

    private List<String> getPermissions(Collection<UserRole> roles) {
        List<String> perms = new ArrayList<>();
        List<Permission> collection = new ArrayList<>();
        for (UserRole role : roles) {
            collection.addAll(role.getPermissions());
        }
        for (Permission item : collection) {
            perms.add(item.getName());
        }
        return perms;
    }
}