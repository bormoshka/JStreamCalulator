package ru.ulmc.bank.ui.data.provider;

import ru.ulmc.bank.ui.data.Text;

/**
 * Created by User on 02.04.2017.
 */
public class RuText implements Text {
    @Override
    public String userName() {
        return "Имя пользователя";
    }

    @Override
    public String password() {
        return "Пароль";
    }

    @Override
    public String singIn() {
        return "Войти";
    }

    @Override
    public String logout() {
        return "Выйти из системы";
    }

    @Override
    public String appFullName() {
        return "АС управления котировками \"Курсы\"";
    }

    @Override
    public String appShortNameHtml() {
        return "АСУ <strong>\"Курсы\"</strong>";
    }

    @Override
    public String authErrorBaseText() {
        return "<span>Пользователь с указанным именем и паролем не найден.</span>";
    }

    @Override
    public String authErrorHeader() {
        return "Вход в систему не выполнен!";
    }

    @Override
    public String authErrorSystemFault(String errorText) {
        return "<span><b>Произошла ошибка авторизации:</b> " + errorText + "</span>";
    }
}
