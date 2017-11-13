package ru.ulmc.bank.core.common;

/**
 * Список возможных разрешений системы
 */
public class Perms {
    public static final String SYMBOL_GRID_CREATE = "SYMBOL_GRID_CREATE";
    public static final String SYMBOL_GRID_UPDATE = "SYMBOL_GRID_UPDATE";
    public static final String SYMBOL_GRID_DELETE = "SYMBOL_GRID_DELETE";

    public static final String FIN_CURRENCY_WRITE = "FIN_CURRENCY_WRITE";
    public static final String FIN_CURRENCY_READ = "FIN_CURRENCY_READ";

    public static final String SYS_USER_CREATE = "SYS_USER_CREATE";
    public static final String SYS_USER_EDIT = "SYS_USER_EDIT";
    public static final String SYS_USER_READ = "SYS_USER_READ";

    public static final String CHECK_FIN_CURRENCY_READ = "hasAuthority('" + Perms.FIN_CURRENCY_READ + "')";
    public static final String CHECK_FIN_CURRENCY_WRITE = "hasAuthority('" + Perms.FIN_CURRENCY_WRITE + "')";

}
