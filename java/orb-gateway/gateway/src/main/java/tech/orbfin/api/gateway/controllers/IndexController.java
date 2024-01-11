package tech.orbfin.api.gateway.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String index() {
        return "index";
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<String> checkJwt(@RequestBody String jwt) {
            return new ResponseEntity<>(jwt, HttpStatus.OK);
    }
}