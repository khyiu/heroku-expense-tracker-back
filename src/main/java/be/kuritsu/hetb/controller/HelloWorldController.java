package be.kuritsu.hetb.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class HelloWorldController {

    @Value("${server.port}")
    private Integer serverPort;

    @GetMapping
    public String sayHello() {
        return "Hello, it is " + LocalDateTime.now() + ", I'm listening to port: " + serverPort;
    }
}
