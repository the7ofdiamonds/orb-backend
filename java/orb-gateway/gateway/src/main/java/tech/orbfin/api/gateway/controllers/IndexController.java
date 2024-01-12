package tech.orbfin.api.gateway.controllers;

import org.springframework.web.bind.annotation.*;

@RequestMapping
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
