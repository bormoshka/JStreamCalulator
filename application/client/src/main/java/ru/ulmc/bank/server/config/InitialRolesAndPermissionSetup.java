package ru.ulmc.bank.server.config;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ulmc.bank.core.common.Perms;
import ru.ulmc.bank.core.common.Roles;
import ru.ulmc.bank.entities.persistent.system.Permission;
import ru.ulmc.bank.entities.persistent.system.User;
import ru.ulmc.bank.entities.persistent.system.UserRole;
import ru.ulmc.bank.dao.repository.PermissionRepository;
import ru.ulmc.bank.dao.repository.UserRepository;
import ru.ulmc.bank.dao.repository.UserRoleRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс, отвечающий за инициализацию пользовательский холей и разрешений.
 */
@Component
@Profile("dev")
public class InitialRolesAndPermissionSetup implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private boolean alreadySetup = false;

    @Autowired
    public InitialRolesAndPermissionSetup(UserRepository userRepository,
                                          UserRoleRepository roleRepository,
                                          PermissionRepository permissionRepository,
                                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;

        Permission finCurrencyRead = createPermissionIfNotFound(Perms.FIN_CURRENCY_READ);
        Permission finCurrencyWrite = createPermissionIfNotFound(Perms.FIN_CURRENCY_WRITE);
        Permission sysUserCreate = createPermissionIfNotFound(Perms.SYS_USER_CREATE);
        Permission sysUserEdit = createPermissionIfNotFound(Perms.SYS_USER_EDIT);
        Permission sysUserRead = createPermissionIfNotFound(Perms.SYS_USER_READ);

        Set<Permission> adminPermissions = Sets.newHashSet(sysUserCreate, sysUserEdit, sysUserRead);
        Set<Permission> managerPermissions = Sets.newHashSet(finCurrencyRead, finCurrencyWrite, sysUserRead);
        Set<Permission> auditorPermissions = Sets.newHashSet(finCurrencyRead, sysUserRead);
        Set<Permission> marketingPermissions = Sets.newHashSet(finCurrencyRead, sysUserRead);

        createRoleIfNotFound(Roles.EMPTY, Collections.emptySet());
        createRoleIfNotFound(Roles.ADMIN, adminPermissions);
        createRoleIfNotFound(Roles.AUDITOR, auditorPermissions);
        createRoleIfNotFound(Roles.MANAGER, managerPermissions);
        createRoleIfNotFound(Roles.MARKETING, marketingPermissions);

        createSystemAdmin();
        createDemoUser();
        alreadySetup = true;
    }

    private void createSystemAdmin() {
        UserRole adminRole = roleRepository.findByName(Roles.ADMIN);
        User user = new User();
        user.setLogin("admin");
        user.setFullName("System Admin");
        user.setRoles(Collections.singleton(adminRole));
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);
    }

    private void createDemoUser() {
        UserRole managerRole = roleRepository.findByName(Roles.MANAGER);
        UserRole marketingRole = roleRepository.findByName(Roles.MARKETING);
        UserRole auditorRole = roleRepository.findByName(Roles.AUDITOR);
        User user = new User();
        user.setLogin("manager");
        user.setFullName("Demo manager");
        user.setRoles(new HashSet<>(Arrays.asList(managerRole, marketingRole, auditorRole)));
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode("manager"));
        userRepository.save(user);
    }

    @Transactional
    private Permission createPermissionIfNotFound(String name) {
        Permission permission = permissionRepository.findByName(name);
        if (permission == null) {
            permission = new Permission(name);
            permissionRepository.save(permission);
        }
        return permission;
    }

    @Transactional
    private UserRole createRoleIfNotFound(String name, Set<Permission> permissions) {
        UserRole role = roleRepository.findByName(name);
        if (role == null) {
            role = new UserRole(name);
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
        return role;
    }
}
