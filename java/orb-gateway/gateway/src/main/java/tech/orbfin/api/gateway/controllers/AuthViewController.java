package tech.orbfin.api.gateway.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class AuthViewController {

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
}
