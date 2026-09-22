package com.twittvl.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {
    @GetMapping("/favicon.ico")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void Favicon() {
    }
}
