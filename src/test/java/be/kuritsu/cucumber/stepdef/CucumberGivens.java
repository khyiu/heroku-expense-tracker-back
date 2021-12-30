package be.kuritsu.cucumber.stepdef;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.Collections;
import java.util.Set;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
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
    public void an_authenticated_user_with_role(String username, String role) {
        state.setCurrentUsername(username);

        AccessToken accessToken = new AccessToken();
        accessToken.exp(Long.MAX_VALUE);
        accessToken.issuer("kuritsu");
        accessToken.setPreferredUsername(username);
        RefreshableKeycloakSecurityContext keycloakSecurityContext = new RefreshableKeycloakSecurityContext(
                null,
                null,
                "dummy_token_string",
                accessToken,
                "dummy_id_token_string",
                new IDToken(),
                "dummy_refresh_token"
        );
        SimpleKeycloakAccount keycloakAccount = new SimpleKeycloakAccount(new KeycloakPrincipal<>(username, keycloakSecurityContext),
                Set.of("ROLE_" + role),
                keycloakSecurityContext);
        KeycloakAuthenticationToken keycloakAuthenticationToken = new KeycloakAuthenticationToken(keycloakAccount, false, Collections.singletonList(new KeycloakRole("ROLE_" + role)));

        state.setCurrentUserRequestPostProcessor(authentication(keycloakAuthenticationToken));
    }
}
