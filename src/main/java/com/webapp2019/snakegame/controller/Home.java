package com.webapp2019.snakegame.controller;

import com.webapp2019.snakegame.SnakeGameApplication;
import com.webapp2019.snakegame.model.Login;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;

@Controller
public class Home {
    @GetMapping("")
    public String RedirToLogin() {
        return "redirect:login";
    }

    @GetMapping("/login")
    public String LoginForm(Model model) {
        model.addAttribute("login",new Login());
        return "login";
    }

    @PostMapping("/login")
    public String LoginSubmit(@ModelAttribute Login user) {
        if(SnakeGameApplication.db_server.checkLogin(user.getUser(), user.getPassword()))
        {
            System.out.println("logged in");
            SnakeGameApplication.users.add(user);
            return "redirect:game";
        }
        else
        {
            System.out.println("nope");
            return "wrong";
        }
    }

    @GetMapping("/adduser")
    public String AddUserForm(Model model) {
        //user2=new Login();
        model.addAttribute("login",new Login());
        return "adduser";
    }

    @PostMapping("/adduser")
    public ModelAndView AddUser(@ModelAttribute Login user) {
        ModelAndView modelAndView = new ModelAndView("userAdded");
        String success= SnakeGameApplication.db_server.addUser(user.getUser(),user.getPassword());
        modelAndView.addObject("message", success);
        System.out.println(success);
        return modelAndView;
    }

    @GetMapping("/game")
    public String Game() {
        return "game";
    }
}