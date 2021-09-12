package be.kuritsu.cucumber.stepdef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.testutil.ExpenseRequestFactory;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SpringBootCucumberTestDefinitions extends CucumberStepDefinitions {

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Given("a non authenticated user")
    public void given_non_authenticated_user() {
        currentUserRequestPostProcessor = null;
    }

    @Given("an authenticated user, {string}, with role {string}")
    public void an_authenticated_user_bob_with_role_expense_tracker_test_user(String username, String role) {
        this.currentUserRequestPostProcessor = user(username).roles(role);
    }

    @When("he sends a request to register any expense")
    public void register_any_expense() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/expenses");

        if (currentUserRequestPostProcessor != null) {
            requestBuilder.with(currentUserRequestPostProcessor);
        }

        ExpenseRequest expenseRequest = ExpenseRequestFactory.getRandomValidExpenseRequest();
        requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest));

        currentMvcResult = mockMvc.perform(requestBuilder)
                .andReturn();
    }

    @Then("he gets a response with status {int}")
    public void assertResponse(int responseStatusCode) {
        assertThat(this.currentMvcResult).isNotNull();
        assertThat(this.currentMvcResult.getResponse().getStatus()).isEqualTo(responseStatusCode);
    }

    @Then("response contains redirect URL {string}")
    public void assertRedirectURL(String redirectURL) {
        assertThat(this.currentMvcResult).isNotNull();
        assertThat(this.currentMvcResult.getResponse().getRedirectedUrl()).isEqualTo(redirectURL);
    }
}
