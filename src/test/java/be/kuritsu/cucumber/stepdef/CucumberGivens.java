package be.kuritsu.cucumber.stepdef;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import be.kuritsu.cucumber.CucumberState;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class CucumberGivens extends CucumberStepDefinitions {

    private final CucumberState state;

    @Autowired
    public CucumberGivens(CucumberState state) {
        this.state = state;
    }

    @Before
    public void setup() {
        if (state.getMockMvc() == null) {
            MockMvc mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();
            state.setMockMvc(mockMvc);
        }
    }

    @Given("a non authenticated user")
    public void given_non_authenticated_user() {
        state.setCurrentUserRequestPostProcessor(null);
    }

    @Given("an authenticated user, {string}, with role {string}")
    public void an_authenticated_user_bob_with_role_expense_tracker_test_user(String username, String role) {
        state.setCurrentUserRequestPostProcessor(user(username).roles(role));
    }
}