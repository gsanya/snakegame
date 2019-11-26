package com.webapp2019.snakegame.controller;

import com.webapp2019.snakegame.SnakeGameApplication;
import com.webapp2019.snakegame.model.SignIn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Home {
    @GetMapping("")
    public String redirectToGame() {
        return "redirect:game";
    }

    @GetMapping("/game")
    public String Game() {
        return "game";
    }

    @GetMapping("/signin")
    public String signInForm(Model model) {
        model.addAttribute("signin",new SignIn());
        return "signin";
    }

    @PostMapping("/signin")
    public String signInSubmit(@ModelAttribute SignIn user) {
        if(SnakeGameApplication.db_server.checkLogin(user.getUser(), user.getPassword()))
        {
            System.out.println("signed in");
            SnakeGameApplication.users.add(user);
            return "redirect:game";
        }
        else
        {
            System.out.println("nope");
            return "redirect:passworderror";
        }
    }

    @GetMapping("/passworderror")
    public String passwordErrorForm() {
        return "passworderror";
    }

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        //user2=new Login();
        model.addAttribute("signin",new SignIn());
        return "signup";
    }

    @PostMapping("/adduser")
    public ModelAndView AddUser(@ModelAttribute SignIn user) {
        ModelAndView modelAndView = new ModelAndView("user_added");
        String success= SnakeGameApplication.db_server.addUser(user.getUser(),user.getPassword());
        modelAndView.addObject("message", success);
        System.out.println(success);
        return modelAndView;
    }
}