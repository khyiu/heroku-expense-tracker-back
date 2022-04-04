package be.kuritsu.hetb.service;

import java.time.format.DateTimeFormatter;

public class ExpenseConstants {
    private ExpenseConstants() {
    }

    public static final DateTimeFormatter EXPENSE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
