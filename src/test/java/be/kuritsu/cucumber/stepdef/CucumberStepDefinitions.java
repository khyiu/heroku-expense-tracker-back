package be.kuritsu.cucumber.stepdef;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import be.kuritsu.cucumber.CucumberSpringConfig;
import be.kuritsu.hetb.HerokuExpenseTrackerBackApplication;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = { HerokuExpenseTrackerBackApplication.class, CucumberSpringConfig.class })
@ActiveProfiles("test")
public class CucumberStepDefinitions {

}
