package be.kuritsu.cucumber.stepdef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import be.kuritsu.cucumber.CucumberSpringConfig;
import be.kuritsu.hetb.HerokuExpenseTrackerBackApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = { HerokuExpenseTrackerBackApplication.class, CucumberSpringConfig.class })
@ActiveProfiles("test")
public abstract class CucumberStepDefinitions {

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;
}
