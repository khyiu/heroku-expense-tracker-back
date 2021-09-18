package be.kuritsu.cucumber;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.cucumber.java.ParameterType;

public class CucumberParameterTypes {

    @ParameterType(value = "-?\\d+(?:\\.\\d{2})?|null")
    public BigDecimal nullableAmount(String amount) {
        return amount.equals("null") ? null : BigDecimal.valueOf(Double.parseDouble(amount));
    }

    @ParameterType(value = "\\d{2}/\\d{2}/\\d{4}|null")
    public LocalDate nullableDate(String date) {
        return date.equals("null") ? null : LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @ParameterType(value = "\\w+(?:;\\w+)*|null|;")
    public List<String> nullableTags(String tags) {
        if (tags.equals("null")) {
            return null;
        }

        if (tags.equals(";")) {
            return Collections.emptyList();
        }

        return Arrays.asList(tags.split(";"));
    }
}
