package tech.orbfin.api.gateway.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String index() {
        return "index";
    }
}
