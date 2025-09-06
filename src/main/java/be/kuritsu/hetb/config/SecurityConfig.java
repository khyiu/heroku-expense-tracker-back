package be.kuritsu.hetb.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    public static final String ROLE_EXPENSE_TRACKER_USER = "ROLE_EXPENSE-TRACKER-USER";
    public static final String EXPENSE_TRACKER_ADMIN = "EXPENSE-TRACKER-ADMIN";

    public interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
    }

    @Bean
    public AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            Optional<Map<String, Object>> realmAccess = Optional.ofNullable((Map<String, Object>) claims.get("realm_access"));
            Optional<List<String>> roles = realmAccess.flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")));
            return roles.stream()
                    .flatMap(Collection::stream)
                    .map(role -> new SimpleGrantedAuthority(("ROLE_" + role).toUpperCase(Locale.ROOT)))
                    .map(GrantedAuthority.class::cast)
                    .toList();
        };
    }

    @Bean
    public JwtAuthenticationConverter authenticationConverter(Converter<Map<String, Object>, Collection<GrantedAuthority>> authoritiesConverter) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> authoritiesConverter.convert(jwt.getClaims()));
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain webOAuth2FilterChain(final HttpSecurity httpSecurity,
                                                    final Converter<Jwt, AbstractAuthenticationToken> authenticationConverter,
                                                    final Environment environment) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator").hasRole(EXPENSE_TRACKER_ADMIN)
                        .requestMatchers("/actuator/**").hasRole(EXPENSE_TRACKER_ADMIN)
                        .requestMatchers("/**").authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwtDecoder -> jwtDecoder.jwtAuthenticationConverter(authenticationConverter)))
                .cors(Customizer.withDefaults());

        // when Spring "test" profile is active -> allow access to /h2-console URL
        if (Arrays.stream(environment.getActiveProfiles()).toList().contains("test")) {
            httpSecurity.authorizeHttpRequests(auth -> auth.requestMatchers(toH2Console()).permitAll())
                    .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        }

        return httpSecurity
                .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:80", "http://postgres:80"));
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}