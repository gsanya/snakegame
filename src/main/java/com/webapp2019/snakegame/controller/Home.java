package com.webapp2019.snakegame.controller;

import com.webapp2019.snakegame.SnakeGameApplication;
import com.webapp2019.snakegame.model.Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Home {
    public Login user;
    @GetMapping("/login")
    public String loginForm(Model model) {
        user=new Login();
        model.addAttribute("login",user);
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute Login user) {
        if(SnakeGameApplication.db_server.checkLogin(user.getUser(), user.getPassword()))
        {
            System.out.println("logged in");
            return "game";

        }
        else
        {
            SnakeGameApplication.db_server.addUser(user.getUser(),user.getPassword());

            System.out.println("nope");
            return "wrong";
        }
    }

}