package tech.orbfin.api.gateway.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class ControllerAuthView {

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/forgot-password")
    public String forgot() { return "forgot";}

    @GetMapping("/change-password")
    public String change() { return "change";}
}
