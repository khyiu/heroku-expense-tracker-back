package be.kuritsu.hetb.security;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityContextService")
public class SecurityContextServiceImpl implements SecurityContextService {

    @Override
    public String getAuthenticatedUserName() {
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        SimpleKeycloakAccount keycloakAccount = (SimpleKeycloakAccount) keycloakAuthenticationToken.getDetails();
        return keycloakAccount.getKeycloakSecurityContext()
                .getToken()
                .getPreferredUsername();
    }
}
