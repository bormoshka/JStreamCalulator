package ru.ulmc.bank.ui.data;

import ru.ulmc.bank.ui.data.provider.RuText;

/**
 * Created by User on 02.04.2017.
 */
public interface Text {
    Text ru = new RuText();

    String userName();

    String password();

    String singIn();

    String logout();

    String appFullName();

    String appShortNameHtml();

    String authErrorBaseText();

    String authErrorHeader();

    String authErrorSystemFault(String errorText);

}
