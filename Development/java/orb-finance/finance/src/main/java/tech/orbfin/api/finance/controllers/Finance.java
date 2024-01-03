package tech.orbfin.api.finance.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Finance {

    @GetMapping("/")
    public String index(){
        return "Finance Index Page";
    }
}
