package ru.ulmc.bank.entities.persistent.system;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * Разрешение/права на выполнение действий.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "SYS_PERMISSION",
        indexes = {@Index(name = "PERMISSION_ID_INDX", columnList = "ID", unique = true)})
@SequenceGenerator(name = "SEQ_PERMISSION", allocationSize = 1)
public class UserRole {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    @ManyToMany
    @JoinTable(
            name = "SYS_ROLES_PERMISSIONS",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID", referencedColumnName = "ID"))
    private Set<Permission> permissions;

    public UserRole() {
    }

    public UserRole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserRole{" + name + '}';
    }
}
