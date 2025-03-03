package be.kuritsu.cucumber.stepdef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.Before;

public class CucumberBefores {
    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberBefores.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int randomServerPort;

    @Before
    public void prepareTest() {
        LOGGER.info("Test application started at port: {}", randomServerPort);

        jdbcTemplate.execute("DELETE FROM het.expense_tag");
        jdbcTemplate.execute("DELETE FROM het.expense");
    }
}
