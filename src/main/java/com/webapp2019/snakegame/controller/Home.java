package com.webapp2019.snakegame.controller;

import com.webapp2019.snakegame.model.Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Le on 2/20/2016.
 */

@Controller
public class Home {

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("login",new Login());
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute Login greeting) {
        return "result";
    }

}