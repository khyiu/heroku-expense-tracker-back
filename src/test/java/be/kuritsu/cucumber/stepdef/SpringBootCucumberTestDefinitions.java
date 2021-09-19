package be.kuritsu.cucumber.stepdef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.testutil.ExpenseRequestFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

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

    @When("he sends a request to register an expense with {nullableDate}, {nullableAmount}, {nullableTags} and {int}")
    public void register_a_parameterized_expense(LocalDate date, BigDecimal amount, List<String> tags, int randomDescriptionLength) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/expenses");

        if (currentUserRequestPostProcessor != null) {
            requestBuilder.with(currentUserRequestPostProcessor);
        }

        ExpenseRequest expenseRequest = ExpenseRequestFactory.getExpenseRequest(date, amount, tags, randomDescriptionLength);
        requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest));

        currentMvcResult = mockMvc.perform(requestBuilder)
                .andReturn();
    }

    @When("he sends a request to register an expense with {nullableDate}, {nullableAmount}, {nullableTags}, {string}, {} and {}")
    public void register_a_parameterized_expense(LocalDate date,
            BigDecimal amount,
            List<String> tags,
            String description,
            Boolean paidWithCreditCard,
            Boolean creditCardStatementIssued) throws Exception {
        ExpenseRequest expenseRequest = ExpenseRequestFactory.getExpenseRequest(date, amount, tags, description, paidWithCreditCard, creditCardStatementIssued);

        MockHttpServletRequestBuilder requestBuilder = post("/expenses");

        if (currentUserRequestPostProcessor != null) {
            requestBuilder.with(currentUserRequestPostProcessor);
        }

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

    @Then("he receives the persisted expense with {nullableDate}, {nullableAmount}, {nullableTags}, {string}, {} and {}")
    public void assertPersistedExpense(LocalDate date,
            BigDecimal amount,
            List<String> tags,
            String description,
            Boolean paidWithCreditCard,
            Boolean creditCardStatementIssued) throws UnsupportedEncodingException, JsonProcessingException {

        ExpenseResponse expenseResponse = objectMapper.readValue(currentMvcResult.getResponse().getContentAsString(), ExpenseResponse.class);
        assertThat(expenseResponse.getId()).isNotNull();
        assertThat(expenseResponse.getVersion()).isNotNull();
        assertThat(expenseResponse.getDate()).isEqualTo(date);
        assertThat(expenseResponse.getAmount()).isEqualTo(amount);
        assertThat(expenseResponse.getTags()).containsExactlyElementsOf(tags);
        assertThat(expenseResponse.getDescription()).isEqualTo(description);
        assertThat(expenseResponse.getPaidWithCreditCard()).isEqualTo(paidWithCreditCard);
        assertThat(expenseResponse.getCreditCardStatementIssued()).isEqualTo(creditCardStatementIssued);
    }
}
