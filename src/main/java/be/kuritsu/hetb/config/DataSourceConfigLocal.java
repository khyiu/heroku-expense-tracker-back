package be.kuritsu.hetb.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
public class DataSourceConfigLocal {

    @Bean
    public DataSource getDataSource(@Value("${db.url}") String dbUrl,
            @Value("${db.user}") String dbUser,
            @Value("${db.password}") String dbPassword,
            @Value("${db.driver}") String dbDriver) {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(dbDriver);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUser);
        dataSourceBuilder.password(dbPassword);
        return dataSourceBuilder.build();
    }
}
