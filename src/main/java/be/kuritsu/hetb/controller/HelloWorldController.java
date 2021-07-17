package be.kuritsu.hetb.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class HelloWorldController {

    @GetMapping
    public String sayHello() {
        return "Hello, it is " + LocalDateTime.now();
    }
}
