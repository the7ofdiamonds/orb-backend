package tech.orbfin.api.gateway.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model) {         // For example, you might want to check the credentials

        if ("yourUsername".equals(username) && "yourPassword".equals(password)) {// Successful login
            return "redirect:/";
        } else {// Failed login
            model.addAttribute("error", "Invalid username or password");
            return "index";
        }
    }
}