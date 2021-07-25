package be.kuritsu.hetb.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("heroku")
@Configuration
public class DataSourceConfigHeroku {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigHeroku.class);

    @Bean
    public DataSource getDataSource(@Value("${db.url}") String dbUrl, @Value("${db.driver}") String dbDriver) {
        LOGGER.info("Current DB URL: " + dbUrl);

        String [] tokens = dbUrl.split("//");
        String [] dbDataTokens = tokens[1].split("@");
        String dbCredentials = dbDataTokens[0];
        String jdbcUrl = dbDataTokens[1];
        String [] dbCredentialsTokens = dbCredentials.split(":");
        String dbUser = dbCredentialsTokens[0];
        String dbPassword = dbCredentialsTokens[1];

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(dbDriver);
        dataSourceBuilder.url(jdbcUrl);
        dataSourceBuilder.username(dbUser);
        dataSourceBuilder.password(dbPassword);
        return dataSourceBuilder.build();
    }
}
