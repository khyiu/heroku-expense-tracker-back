package be.kuritsu.cucumber.stepdef;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CucumberState {

    private  MockMvc mockMvc;
    private  RequestPostProcessor currentUserRequestPostProcessor;
    private  MvcResult currentMvcResult;
}
