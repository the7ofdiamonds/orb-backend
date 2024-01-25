package tech.orbfin.api.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(
        origins = "https://the7ofdiamonds.development",
        allowCredentials = "true",
        methods = {RequestMethod.GET, RequestMethod.POST},
        allowedHeaders = {"Authorization", "Content-Type"}
)
@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> index() {
        Map<String, String> response = new HashMap<>();
        response.put("pagename", "Accounts");
        return response;
    }
}


