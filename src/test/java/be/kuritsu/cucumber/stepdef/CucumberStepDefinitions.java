package be.kuritsu.cucumber.stepdef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import be.kuritsu.hetb.HerokuExpenseTrackerBackApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HerokuExpenseTrackerBackApplication.class)
public abstract class CucumberStepDefinitions {

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    protected RequestPostProcessor currentUserRequestPostProcessor;

    protected MvcResult currentMvcResult;
}
