package com.io.github.amandotzip.touchgrass.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {
    @RequestMapping(value = "/{path:[^\\\\.]*}")
    public String forward() {
        // Forward to Angular's index.html
        return "forward:/index.html";
    }
}
