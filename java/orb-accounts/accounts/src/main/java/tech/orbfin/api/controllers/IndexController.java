package tech.orbfin.api.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {

    @PostMapping("/")
    public @ResponseBody String index(@RequestBody String accountNumber) {
        System.out.println(accountNumber);
        return "{\"message\": \"Accounts endpoint reached.\"}";
    }
}



