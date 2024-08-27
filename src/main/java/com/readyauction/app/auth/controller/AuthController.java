package com.readyauction.app.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {


    @GetMapping("/login")
    public void login(@RequestParam(value = "success", required = false) Boolean success,
                        @RequestParam(value = "error", required = false) Boolean error,
                        Model model) {
        if (success != null && success) {
            model.addAttribute("loginSuccess", true);
        }

        if (error != null && error) {
            model.addAttribute("loginFailure", true);
        }
    }
}
