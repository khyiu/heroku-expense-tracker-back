package be.kuritsu.cucumber.stepdef;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    @When("he sends a request to edit something in the expense with id={string}")
    public void edit_expense(String expenseId) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = put("/expense/{expenseId}", expenseId);

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

    @When("he sends a request to delete the expense with id={string}")
    public void delete_expense(String expenseId) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = delete("/expense/{expenseId}", expenseId);

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }

    @When("he sends a request to delete the last expense created by {string}")
    public void delete_last_expense_created_by(String username) throws Exception {
        ExpenseResponse expenseResponse = objectMapper.readValue(state.getUserLastCreatedExpenseResults().get(username).getResponse().getContentAsString(), ExpenseResponse.class);
        MockHttpServletRequestBuilder requestBuilder = delete("/expense/{expenseId}", expenseResponse.getId());

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }

    @When("he sends a request to register an expense with {nullableDate}, {nullableAmount}, {nullableStringList} and {int}")
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

    @When("he sends a request to register an expense with {nullableDate}, {nullableAmount}, {nullableStringList}, {string}, {} and {}")
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
    public void retrieve_last_expense_created_by(String username) throws Exception {
        MvcResult lastMvcResult = state.getUserLastCreatedExpenseResults().get(username);
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

    @When("he sends a request to edit the last expense created by {string} with {nullableDate}, {nullableAmount}, {nullableStringList}, {string}, {} and {}")
    public void register_a_parameterized_expense(String username,
            LocalDate date,
            BigDecimal amount,
            List<String> tags,
            String description,
            Boolean paidWithCreditCard,
            Boolean creditCardStatementIssued) throws Exception {
        ExpenseResponse expenseResponse = objectMapper.readValue(state.getCurrentUserResults().get(username).getResponse().getContentAsString(), ExpenseResponse.class);
        MockHttpServletRequestBuilder requestBuilder = put("/expense/{expenseId}", expenseResponse.getId());

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        ExpenseRequest expenseRequestUpdate = new ExpenseRequest()
                .version(expenseResponse.getVersion())
                .date(date)
                .amount(amount)
                .tags(tags)
                .description(description)
                .paidWithCreditCard(paidWithCreditCard)
                .creditCardStatementIssued(creditCardStatementIssued);

        requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequestUpdate));

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }

    @When("he sends a request to retrieve his expenses with page number={int}, "
            + "page size={int}, "
            + "sortBy={sortBy}, "
            + "sortDirection={sortDirection}, "
            + "tag filters={nullableStringList} "
            + "and description filter={nullableString}")
    public void retrieve_expenses(int pageNumber, int pageSize, String sortBy, String sortDirection, List<String> tagFilters, String descriptionFilter) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/expenses")
                .param("pageNumber", String.format("%d", pageNumber))
                .param("pageSize", String.format("%d", pageSize))
                .param("sortBy", sortBy)
                .param("sortDirection", sortDirection);

        if (tagFilters != null) {
            requestBuilder.param("pageFilters", tagFilters.toArray(new String[] {}));
        }

        if (descriptionFilter != null) {
            requestBuilder.param("descriptionFilter", descriptionFilter);
        }

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }

    @When("he sends a request to retrieve his expenses balance")
    public void retrieve_balance() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/balance");

        if (state.getCurrentUserRequestPostProcessor() != null) {
            requestBuilder.with(state.getCurrentUserRequestPostProcessor());
        }

        state.setCurrentMvcResult(state.getMockMvc()
                .perform(requestBuilder)
                .andReturn());
    }
}
