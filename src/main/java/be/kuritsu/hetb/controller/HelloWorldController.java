package be.kuritsu.hetb.controller;

import static be.kuritsu.hetb.config.SecurityConfig.ROLE_EXPENSE_TRACKER_USER;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class HelloWorldController {

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${db.url}")
    private String dbURL;

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @GetMapping("hello")
    public String sayHello() {
        return "Hello, it is " + LocalDateTime.now() + ", I'm listening to port: " + serverPort + " - DB URL: " + dbURL;
    }
}
