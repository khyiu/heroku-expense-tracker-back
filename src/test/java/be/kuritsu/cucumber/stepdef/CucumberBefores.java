package be.kuritsu.cucumber.stepdef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.Before;

public class CucumberBefores {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void openTheApplication() {
        jdbcTemplate.execute("DELETE FROM het.expense_tag");
        jdbcTemplate.execute("DELETE FROM het.expense");
    }
}
