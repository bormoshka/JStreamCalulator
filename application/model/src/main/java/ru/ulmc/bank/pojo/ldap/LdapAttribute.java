package ru.ulmc.bank.pojo.ldap;

import lombok.Data;

import java.io.Serializable;

@Data
public class LdapAttribute implements Serializable {
    private String id;
    private String value;

    public LdapAttribute() {
    }

    public LdapAttribute(String id, String value) {
        this.id = id;
        this.value = value;
    }
}
