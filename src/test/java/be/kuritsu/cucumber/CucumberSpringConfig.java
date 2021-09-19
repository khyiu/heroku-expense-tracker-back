package be.kuritsu.cucumber;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CucumberSpringConfig {

    @Bean
    public CucumberState getCucumberState() {
        return new CucumberState();
    }
}
