package com.thors.secure_store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @GetMapping("/")
    public String getHelloWorld(){
        return "hello world";
    }
}
