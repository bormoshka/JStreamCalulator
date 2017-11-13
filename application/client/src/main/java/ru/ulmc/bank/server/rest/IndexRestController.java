package ru.ulmc.bank.server.rest;

//import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * IndexController
 */
@Controller
@RequestMapping(value = "/")
public class IndexRestController {
    private static final String VIEW = "index";

    /*@RequestMapping(value = "*", method = RequestMethod.GET)
    public String index(Model data) {
        return userSession.isAuthenticated() ? "redirect:app/" : "redirect:auth/";
    }*/
}
