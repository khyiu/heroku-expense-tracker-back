package be.kuritsu.config;

import java.util.Locale;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * After upgrade to H2 2.x.x, identifier must be handled in uppercase to avoid issues with protected reserved keywords. For instance, column named "VALUE"...
 */
public class CamelCaseToUnderscoresNamingStrategyForH2Version2 extends CamelCaseToUnderscoresNamingStrategy {

    @Override
    protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        return new Identifier(name.toUpperCase(Locale.ROOT), quoted);
    }
}
