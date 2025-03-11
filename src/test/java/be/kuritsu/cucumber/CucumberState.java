package be.kuritsu.cucumber;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import be.kuritsu.het.model.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CucumberState {

    private MockMvc mockMvc;
    private RequestPostProcessor currentUserRequestPostProcessor;
    private MvcResult currentMvcResult;
    private String currentUsername;
    private Map<String, MvcResult> currentUserResults = new HashMap<>();
    private Map<String, MvcResult> userLastCreatedExpenseResults = new HashMap<>();
    private Map<String, Map<String, Tag>> userTags = new HashMap<>();

    public void setCurrentMvcResult(MvcResult currentMvcResult) {
        if (this.currentUsername != null) {
            currentUserResults.put(this.currentUsername, currentMvcResult);
        }

        if (currentMvcResult.getResponse().getStatus() == HttpStatus.CREATED.value()) {
            userLastCreatedExpenseResults.put(this.currentUsername, currentMvcResult);
        }

        this.currentMvcResult = currentMvcResult;
    }

    public Map<String, MvcResult> getCurrentUserResults() {
        return Collections.unmodifiableMap(currentUserResults);
    }
}
