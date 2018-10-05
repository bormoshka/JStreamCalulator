package ru.ulmc.bank.entities.persistent.system;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

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
public class Permission {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    public Permission(String name) {
        this.name = name;
    }

    public Permission() {
    }
}
