package com.readyauction.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class AppController {

    @GetMapping("/")
    public String index() {
        return "redirect:/auction/auction";
    }

//    @GetMapping("/about")
//    public String about() {
//        return "/about";
//    }
//
//    @GetMapping("/auction-details")
//    public String auctionDetails() {
//        return "/auction-details";
//    }
//
//    @GetMapping("/blog")
//    public String blog() {
//        return "/blog";
//    }
//
//    @GetMapping("/blog-details")
//    public String blogDetails() {
//        return "/blog-details";
//    }
//
//    @GetMapping("/blog-standard")
//    public String blogStandard() {
//        return "/blog-standard";
//    }
//
//    @GetMapping("/privacy")
//    public String privacy() {
//        return "/privacy";
//    }
//
//    @GetMapping("/winner")
//    public String winner() {
//        return "/winner";
//    }
}
