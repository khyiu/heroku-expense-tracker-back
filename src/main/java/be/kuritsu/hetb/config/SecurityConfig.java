package be.kuritsu.hetb.config;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@EnableGlobalMethodSecurity(securedEnabled = true)
@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    public static final String ROLE_EXPENSE_TRACKER_USER = "ROLE_EXPENSE-TRACKER-USER";
    public static final String EXPENSE_TRACKER_ADMIN = "EXPENSE-TRACKER-ADMIN";

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) {
        SimpleAuthorityMapper simpleAuthorityMapper = new SimpleAuthorityMapper();
        simpleAuthorityMapper.setPrefix("");

        KeycloakAuthenticationProvider keycloakAuthenticationProvider = new KeycloakAuthenticationProviderWrapper();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(simpleAuthorityMapper);
        authenticationManagerBuilder.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }

    // Ignore security hotspot about CSRF as this back-end API is intended to an Angular SPA that is deployed separately
    @SuppressWarnings("java:S4502")
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        super.configure(httpSecurity);
        httpSecurity.authorizeRequests()
                .antMatchers("/actuator").hasRole(EXPENSE_TRACKER_ADMIN)
                .antMatchers("/actuator/**").hasRole(EXPENSE_TRACKER_ADMIN)
                .antMatchers("/**").fullyAuthenticated()
                /*
                 By default Spring security enables CSRF. As a consequence, a CSRF token need to be provided
                 in PUT/POST/DELETE requests. If the token is missing or invalid, the request results in HTTP 403 error.
                 Let's disable CSRF for the moment. We'll check later if it's worth switching it back on.
                 */
                .and()
                .csrf().disable();
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    private static class KeycloakAuthenticationProviderWrapper extends KeycloakAuthenticationProvider {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;

            SimpleKeycloakAccount keycloakAccount = (SimpleKeycloakAccount) token.getDetails();
            RefreshableKeycloakSecurityContext keycloakSecurityContext = keycloakAccount.getKeycloakSecurityContext();
            List<GrantedAuthority> grantedAuthorities = keycloakSecurityContext
                    .getToken()
                    .getRealmAccess()
                    .getRoles()
                    .stream()
                    .map(keycloakRole -> new KeycloakRole("ROLE_" + keycloakRole.toUpperCase()))
                    .collect(Collectors.toList());

            return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), grantedAuthorities);
        }
    }
}