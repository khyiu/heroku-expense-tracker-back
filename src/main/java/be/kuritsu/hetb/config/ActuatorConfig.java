package be.kuritsu.hetb.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import be.kuritsu.hetb.actuator.PersistenceActuatorEndpoint;

@Configuration
public class ActuatorConfig {

    @Bean
    public PersistenceActuatorEndpoint getPersistentStorageActuatorEndpoint(@Value("${db.url}") String dbUrl,
                                                                            @Value("${db.user:#{null}}") String dbUser,
                                                                            @Value("${db.password:#{null}}") String dbPassword) {
        return new PersistenceActuatorEndpoint(Map.of(
                PersistenceActuatorEndpoint.StorageType.DB, String.format("%s - %s - %s", dbUrl, dbUser, dbPassword)
        ));
    }
}
