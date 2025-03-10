package be.kuritsu.cucumber.stepdef;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import be.kuritsu.cucumber.CucumberState;

import io.cucumber.java.en.Given;

public class CucumberGivens {

    private final CucumberState state;

    @Autowired
    public CucumberGivens(CucumberState state) {
        this.state = state;
    }

    @Given("a non authenticated user")
    public void given_non_authenticated_user() {
        state.setCurrentUserRequestPostProcessor(null);
    }

    @Given("an authenticated user, {string}, with role {string}")
    public void an_authenticated_user_with_role(String username, String role) {
        state.setCurrentUsername(username);
        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.claim("preferred_username", username))
                .authorities(new SimpleGrantedAuthority(("ROLE_" + role).toUpperCase(Locale.ROOT)));
        state.setCurrentUserRequestPostProcessor(jwtRequestPostProcessor);
    }
}
