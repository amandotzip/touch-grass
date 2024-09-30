package com.io.github.amandotzip.touchgrass.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TouchGrassController {
    @GetMapping("/hello")
    public String index() {
        return "Hello World!";
    }
}
