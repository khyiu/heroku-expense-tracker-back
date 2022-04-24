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

    @ParameterType(value = "[a-zA-Z_0-9-]+(?:;[a-zA-Z_0-9-]+)*|null|;")
    public List<String> nullableStringList(String tags) {
        if (tags.equals("null")) {
            return null;
        }

        if (tags.equals(";")) {
            return Collections.emptyList();
        }

        return Arrays.asList(tags.split(";"));
    }

    @ParameterType(value = "[\\p{Alnum}\\.\\-' ]+|null")
    public String nullableString(String string) {
        return string.equals("null") ? null : string;
    }

    @ParameterType(value = "ASC|DESC")
    public String sortDirection(String sortDirection) {
        return sortDirection;
    }

    @ParameterType(value = "DATE|AMOUNT")
    public String sortBy(String sortBy) {
        return sortBy;
    }

    @ParameterType(value = "true|TRUE|false|FALSE|null|NULL")
    public Boolean nullableBoolean(String value) {
        return value.equalsIgnoreCase("null") ?
                null:
                Boolean.valueOf(value);
    }
}
