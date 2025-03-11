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
import org.springframework.web.context.WebApplicationContext;

import be.kuritsu.cucumber.CucumberState;
import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.het.model.Tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;

public class CucumberThens {

    private final CucumberState state;
    private final WebApplicationContext context;
    private final ObjectMapper objectMapper;

    @Autowired
    public CucumberThens(CucumberState state,
                         WebApplicationContext context,
                         ObjectMapper objectMapper) {
        this.state = state;
        this.context = context;
        this.objectMapper = objectMapper;
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

    @Then("he/she gets a response with status {int}")
    public void assertResponse(int responseStatusCode) {
        assertThat(state.getCurrentMvcResult()).isNotNull();
        assertThat(state.getCurrentMvcResult().getResponse().getStatus()).isEqualTo(responseStatusCode);
    }

    @Then("he receives the persisted expense with {nullableDate}, {nullableAmount}, {nullableStringList}, {nullableString}, {}, {} and {nullableBoolean}")
    public void assertPersistedExpense(LocalDate date,
                                       BigDecimal amount,
                                       List<String> tags,
                                       String description,
                                       Boolean paidWithCreditCard,
                                       Boolean creditCardStatementIssued,
                                       Boolean checked) throws UnsupportedEncodingException, JsonProcessingException {

        ExpenseResponse expenseResponse = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), ExpenseResponse.class);
        assertThat(expenseResponse.getId()).isNotNull();
        assertThat(expenseResponse.getVersion()).isNotNull();
        assertThat(expenseResponse.getDate()).isEqualTo(date);
        assertThat(expenseResponse.getAmount()).isEqualByComparingTo(amount);
        assertThat(expenseResponse.getTags()
                           .stream()
                           .map(Tag::getValue)
                           .toList())
                .containsExactlyElementsOf(tags);
        assertThat(expenseResponse.getDescription()).isEqualTo(description);
        assertThat(expenseResponse.getPaidWithCreditCard()).isEqualTo(paidWithCreditCard);
        assertThat(expenseResponse.getCreditCardStatementIssued()).isEqualTo(creditCardStatementIssued);
        assertThat(expenseResponse.getChecked()).isEqualTo(checked);
    }

    @Then("he receives a list of {int} expense(s)")
    public void assertEmptyListOfExpenses(int expectedNbExpenses) throws Exception {
        ExpenseListResponse expenseListResponse = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), ExpenseListResponse.class);
        assertThat(expenseListResponse.getItems()).hasSize(expectedNbExpenses);
    }

    @Then("he/she receives a list of expenses containing at index {int} an expense with {nullableDate}, {nullableAmount}, {nullableStringList}, {nullableString}, {}, {} and {nullableBoolean}")
    public void assertExpenseFromDashboard(int index,
                                           LocalDate date,
                                           BigDecimal amount,
                                           List<String> tags,
                                           String description,
                                           Boolean paidWithCreditCard,
                                           Boolean creditCardStatementIssued,
                                           Boolean checked) throws Exception {
        ExpenseListResponse expenseListResponse = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), ExpenseListResponse.class);
        ExpenseResponse expenseResponse = expenseListResponse.getItems().get(index);
        assertThat(expenseResponse.getId()).isNotNull();
        assertThat(expenseResponse.getVersion()).isNotNull();
        assertThat(expenseResponse.getDate()).isEqualTo(date);
        assertThat(expenseResponse.getAmount()).isEqualByComparingTo(amount);
        assertThat(expenseResponse.getTags()
                           .stream()
                           .map(Tag::getValue)
                           .toList())
                .containsExactlyElementsOf(tags);
        assertThat(expenseResponse.getDescription()).isEqualTo(description);
        assertThat(expenseResponse.getPaidWithCreditCard()).isEqualTo(paidWithCreditCard);
        assertThat(expenseResponse.getCreditCardStatementIssued()).isEqualTo(creditCardStatementIssued);
        assertThat(expenseResponse.getChecked()).isEqualTo(checked);
    }

    @Then("he receives a balance equal to {nullableAmount}")
    public void assertExpensesBalance(BigDecimal balance) throws Exception {
        BigDecimal fetchedBalance = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), BigDecimal.class);
        assertThat(fetchedBalance).isEqualByComparingTo(balance);
    }

    @Then("he/she receives a list of tags that contains {nullableStringList}")
    public void assertTags(List<String> expectedTags) throws Exception {
        List<Tag> tags = objectMapper.readValue(state.getCurrentMvcResult().getResponse().getContentAsString(), new TypeReference<List<Tag>>() {
        });

        assertThat(tags).hasSize(expectedTags.size());

        for (String expectedTag : expectedTags) {
            assertThat(tags).anySatisfy(tag -> {
                assertThat(tag.getId()).isNotNull();
                assertThat(tag.getValue()).isEqualTo(expectedTag);
            });
        }
    }

    @Then("he/she receives an empty export file")
    public void assertEmptyExportFile() throws Exception {
        String csvFileContent = state.getCurrentMvcResult().getResponse().getContentAsString();
        assertThat(csvFileContent).isBlank();
    }

    @Then("he/she receives an export file that contains {string}")
    public void assertExportFileContains(String content) throws Exception {
        String csvFileContent = state.getCurrentMvcResult().getResponse().getContentAsString();
        assertThat(csvFileContent.trim()).isEqualTo(content);
    }
}
