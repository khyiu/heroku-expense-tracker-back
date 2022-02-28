package be.kuritsu.cucumber.stepdef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import be.kuritsu.cucumber.CucumberState;
import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;

public class CucumberThens extends CucumberStepDefinitions {

    private final CucumberState state;

    @Autowired
    public CucumberThens(CucumberState state) {
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

    @Then("he gets a response with status {int}")
    public void assertResponse(int responseStatusCode) {
        assertThat(state.getCurrentMvcResult()).isNotNull();
        assertThat(state.getCurrentMvcResult().getResponse().getStatus()).isEqualTo(responseStatusCode);
    }

    @Then("response contains redirect URL {string}")
    public void assertRedirectURL(String redirectURL) {
        assertThat(state.getCurrentMvcResult()).isNotNull();
        assertThat(state.getCurrentMvcResult().getResponse().getRedirectedUrl()).isEqualTo(redirectURL);
    }

    @Then("he receives the persisted expense with {nullableDate}, {nullableAmount}, {nullableStringList}, {string}, {} and {}")
    public void assertPersistedExpense(LocalDate date,
            BigDecimal amount,
            List<String> tags,
            String description,
            Boolean paidWithCreditCard,
            Boolean creditCardStatementIssued) throws UnsupportedEncodingException, JsonProcessingException {

        ExpenseResponse expenseResponse = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), ExpenseResponse.class);
        assertThat(expenseResponse.getId()).isNotNull();
        assertThat(expenseResponse.getVersion()).isNotNull();
        assertThat(expenseResponse.getDate()).isEqualTo(date);
        assertThat(expenseResponse.getAmount()).isEqualByComparingTo(amount);
        // todo kyiu: fix
//        assertThat(expenseResponse.getTags()).containsExactlyElementsOf(tags);
        assertThat(expenseResponse.getDescription()).isEqualTo(description);
        assertThat(expenseResponse.getPaidWithCreditCard()).isEqualTo(paidWithCreditCard);
        assertThat(expenseResponse.getCreditCardStatementIssued()).isEqualTo(creditCardStatementIssued);
    }

    @Then("he receives a list of {int} expense(s)")
    public void assertEmptyListOfExpenses(int expectedNbExpenses) throws Exception {
        ExpenseListResponse expenseListResponse = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), ExpenseListResponse.class);
        assertThat(expenseListResponse.getItems()).hasSize(expectedNbExpenses);
    }

    @Then("he receives a list of expenses containing at index {int} an expense with {nullableDate}, {nullableAmount}, {nullableStringList}, {string}, {} and {}")
    public void assertExpenseFromDashboard(int index,
            LocalDate date,
            BigDecimal amount,
            List<String> tags,
            String description,
            Boolean paidWithCreditCard,
            Boolean creditCardStatementIssued) throws Exception {
        ExpenseListResponse expenseListResponse = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), ExpenseListResponse.class);
        ExpenseResponse expenseResponse = expenseListResponse.getItems().get(index);
        assertThat(expenseResponse.getId()).isNotNull();
        assertThat(expenseResponse.getVersion()).isNotNull();
        assertThat(expenseResponse.getDate()).isEqualTo(date);
        assertThat(expenseResponse.getAmount()).isEqualByComparingTo(amount);
        // todo kyiu: fix
//        assertThat(expenseResponse.getTags()).containsExactlyElementsOf(tags);
        assertThat(expenseResponse.getDescription()).isEqualTo(description);
        assertThat(expenseResponse.getPaidWithCreditCard()).isEqualTo(paidWithCreditCard);
        assertThat(expenseResponse.getCreditCardStatementIssued()).isEqualTo(creditCardStatementIssued);
    }

    @Then("he receives a balance equal to {nullableAmount}")
    public void assertExpensesBalance(BigDecimal balance) throws Exception {
        BigDecimal fetchedBalance = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), BigDecimal.class);
        assertThat(fetchedBalance).isEqualByComparingTo(balance);
    }
}
