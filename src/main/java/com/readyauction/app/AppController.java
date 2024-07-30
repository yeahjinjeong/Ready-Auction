package com.readyauction.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class AppController {
    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/404")
    public String pageNotFound() {
        return "404";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/auction")
    public String auction() {
        return "auction";
    }

    @GetMapping("/auction-details")
    public String auctionDetails() {
        return "auction-details";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }

    @GetMapping("/blog-details")
    public String blogDetails() {
        return "blog-details";
    }

    @GetMapping("/blog-standard")
    public String blogStandard() {
        return "blog-standard";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/index-art-dark")
    public String indexArtDark() {
        return "index-art-dark";
    }

    @GetMapping("/index-art-light")
    public String indexArtLight() {
        return "index-art-light";
    }

    @GetMapping("/index-car")
    public String indexCar() {
        return "index-car";
    }

    @GetMapping("/index-land")
    public String indexLand() {
        return "index-land";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/winner")
    public String winner() {
        return "winner";
    }
}
