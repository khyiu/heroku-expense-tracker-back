package be.kuritsu.hetb.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service("securityContextService")
public class SecurityContextServiceImpl implements SecurityContextService {

    @Override
    public String getAuthenticatedUserName() {
        JwtAuthenticationToken authToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return ((Jwt)authToken.getCredentials()).getClaimAsString("preferred_username");
    }
}
