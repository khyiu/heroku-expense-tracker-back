package be.kuritsu.cucumber.stepdef;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import be.kuritsu.cucumber.CucumberState;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.testutil.ExpenseRequestFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.cucumber.java.Before;
import io.cucumber.java.en.When;

public class CucumberWhens extends CucumberStepDefinitions {

    private final CucumberState state;

    @Autowired
    public CucumberWhens(CucumberState state) {
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

    @When("he sends a request to register any expense")
    public void register_any_expense() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/expenses");

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        ExpenseRequest expenseRequest = ExpenseRequestFactory.getRandomValidExpenseRequest();
        requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest));

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }

    @When("he sends a request to register an expense with {nullableDate}, {nullableAmount}, {nullableTags} and {int}")
    public void register_a_parameterized_expense(LocalDate date, BigDecimal amount, List<String> tags, int randomDescriptionLength) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/expenses");

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        ExpenseRequest expenseRequest = ExpenseRequestFactory.getExpenseRequest(date, amount, tags, randomDescriptionLength);
        requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest));

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
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

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest));

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }

    @When("he sends a request to retrieve any expense")
    public void retrieves_any_expense() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/expense/{expenseId}", UUID.randomUUID());

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }

    @When("he sends a request to retrieve the last expense created by {string}")
    public void heSendsARequestToRetrieveTheLastExpenseCreatedBy(String username) throws Exception {
        MvcResult lastMvcResult = state.getCurrentUserResults().get(username);
        ExpenseResponse lastExpenseResponse = objectMapper.readValue(lastMvcResult.getResponse().getContentAsString(), ExpenseResponse.class);
        String expenseId = lastExpenseResponse.getId();

        MockHttpServletRequestBuilder requestBuilder = get("/expense/{expenseId}", expenseId);

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }
}
