package com.readyauction.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HomeController {
    @Value("${profile.value}")
    private String value;

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> home(){
        return ResponseEntity.ok("<h1>⚽️Hello world! App's profile is <mark>%s</mark> \uD83C\uDFB1</h1>".formatted(value));
    }
}
